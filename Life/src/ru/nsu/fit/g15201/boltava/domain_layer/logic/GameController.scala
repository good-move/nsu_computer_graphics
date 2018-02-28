package ru.nsu.fit.g15201.boltava.domain_layer.logic

import java.util.concurrent.{ScheduledFuture, ScheduledThreadPoolExecutor, TimeUnit}

import ru.nsu.fit.g15201.boltava.domain_layer.canvas.{HexagonalGridController, IGridController}
import ru.nsu.fit.g15201.boltava.domain_layer.logic.settings._
import ru.nsu.fit.g15201.boltava.presentation_layer.main.{ICellStateObserver, IGridStateObserver}

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.util.{Failure, Success, Try}

/**
  * Controls logic of the whole game.
  *
  */
class GameController extends IGameLogicController with IFieldStateObserver {

  private val executor = new ScheduledThreadPoolExecutor(1)
  private val fieldUpdateInterval = 1000
  private var updateTask: ScheduledFuture[_] = _

  private var gridController: Option[IGridController] = None
  private val gameSettings = new GameSettings()
  private val boundsSettings = new SettingsBounds

  private var currentCellGrid: Option[Array[Array[Cell]]] = None
  private var cellSelectionMode = CellSelectionMode.TOGGLE

  private var fieldUpdater: ConwayFieldUpdater = _
  private val cellStateObservers = new mutable.HashSet[ICellStateObserver]()
  private val gridStateObservers = new mutable.HashSet[IGridStateObserver]()

  private var gameState = GameState.UNINITIALIZED

  {
    fieldUpdater = new ConwayFieldUpdater
    fieldUpdater.setStateObserver(this)
    initBounds()
    initLifeScores()
    initImpactScores()
  }

  private def initBounds(): Unit = {
    boundsSettings.minBorderSize = GameSettings.MIN_BORDER_SIZE
    boundsSettings.maxBorderSize = GameSettings.MAX_BORDER_SIZE
    boundsSettings.minBorderWidth = GameSettings.MIN_BORDER_WIDTH
    boundsSettings.maxBorderWidth = GameSettings.MAX_BORDER_WIDTH
    boundsSettings.maxGridSize = GameSettings.MAX_GRID_SIZE
  }

  private def initLifeScores(): Unit = {
    gameSettings.lifeScores.maxAliveScore = GameSettings.MAX_ALIVE_SCORE
    gameSettings.lifeScores.minAliveScore = GameSettings.MIN_ALIVE_SCORE
    gameSettings.lifeScores.maxBirthScore = GameSettings.MAX_BIRTH_SCORE
    gameSettings.lifeScores.minBirthScore = GameSettings.MIN_BIRTH_SCORE

    fieldUpdater.updateLifeScores(gameSettings.lifeScores)
  }

  private def initImpactScores(): Unit = {
    gameSettings.impactScores.firstOrderImpact = GameSettings.FIRST_ORDER_IMPACT
    gameSettings.impactScores.secondOrderImpact = GameSettings.SECOND_ORDER_IMPACT

    fieldUpdater.updateImpactScore(gameSettings.impactScores)
  }

  override def getSettingsBounds: SettingsBounds = boundsSettings

  override def setPlaygroundSettings(playgroundSettings: PlaygroundSettings): Unit = {
    Try(validatePlaygroundSettings(playgroundSettings)) match {
      case Success(_) => this.gameSettings.playgroundSettings = playgroundSettings
      case Failure(t) => throw t
    }
  }

  override def getGameSettings: GameSettings = this.gameSettings

  private def validatePlaygroundSettings(settings: PlaygroundSettings) = {
    if (settings.gridWidth <= 0 || settings.gridWidth > GameSettings.MAX_GRID_SIZE ||
      settings.gridHeight <= 0 || settings.gridHeight > GameSettings.MAX_GRID_SIZE) {
      throw new RuntimeException(
        s"Invalid grid dimensions. Grid width and height " +
          s"must be positive integers between 1 and ${GameSettings.MAX_GRID_SIZE}.")
    }

    if (settings.borderWidth <= 0 || settings.borderWidth > GameSettings.MAX_BORDER_WIDTH) {
      throw new RuntimeException(s"Border width must be a positive integer not greater than ${GameSettings.MAX_BORDER_WIDTH}.")
    }

    if (settings.borderSize <= 0 || settings.borderSize > GameSettings.MAX_BORDER_SIZE) {
      throw new RuntimeException(s"Cell side size must be a positive integer not grater than ${GameSettings.MAX_BORDER_SIZE}.")
    }

    if (settings.aliveCells != null) {
      settings.aliveCells.foreach(cell => {
        if (cell._1 < 0 || cell._1 >= settings.gridWidth ||
          cell._2 < 0 || cell._2 >= settings.gridHeight) {
          throw new RuntimeException(s"Cell coordinates out of bounds: $cell " +
            s"(width: ${settings.gridWidth}, height: ${settings.gridHeight}).")
        }
      })
    }

  }

  override def getCells: Array[Array[Cell]] = currentCellGrid.get

  override def setGridController(gridController: IGridController): Unit = {
    this.gridController = Some(gridController)
    fieldUpdater.setGridController(gridController)
  }

  override def getGridController: IGridController = gridController.get

  override def setCellSelectionMode(newCellSelectionMode: CellSelectionMode.Value): Unit = {
    cellSelectionMode = newCellSelectionMode
  }

  override def getCellSelectionMode: CellSelectionMode.Value = cellSelectionMode

  // *************************** Game Lifecycle Routines ***************************

  override def start(): Unit = {
    if (!this.isGameInitialized && !this.isGamePaused) {
      throw new IllegalStateException("Game Field is not initialized")
    }

    if (!isGameRunning) {
      updateTask = executor.scheduleAtFixedRate(fieldUpdater, 0, fieldUpdateInterval, TimeUnit.MILLISECONDS)
      gameState = GameState.RUNNING
    }
  }

  override def pause(): Unit = {
    gameState = GameState.PAUSED
    stopUpdater()
  }

  override def reset(): Unit = {
    if (isGameReset) return
    if (currentCellGrid.isEmpty) return
    gameState = GameState.RESET
    stopUpdater()

    currentCellGrid.get.foreach(_.foreach(cell => {
      val oldState = cell.getState
      cell.setState(State.DEAD)
      if (oldState != cell.getState) {
        notifyCellStateObservers(cell)
      }
    }))
    fieldUpdater.setMainField(currentCellGrid.get)
  }

  override def nextStep(): Unit = {
    if (!isGameInitialized) {
      throw new RuntimeException("Game Field is not initialized")
    }

    fieldUpdater.makeStep()
  }

  override def isGameInitialized: Boolean = gameState != GameState.UNINITIALIZED

  override def isGameRunning: Boolean = gameState == GameState.RUNNING

  override def isGamePaused: Boolean = gameState == GameState.PAUSED

  override def isGameReset: Boolean = gameState == GameState.RESET

  override def isGameFinished: Boolean = gameState == GameState.FINISHED

  // *************************** Private Methods ***************************

  private def setAliveCells(): Unit = {
    if (currentCellGrid.isEmpty) return
    if (gameSettings.playgroundSettings.aliveCells == null) return

    gameSettings.playgroundSettings
      .aliveCells
      .foreach(coords => currentCellGrid.get(coords._1)(coords._2).setState(State.ALIVE))
  }

  private def stopUpdater(): Unit = {
    if (updateTask != null) {
      updateTask.cancel(true)
    }
  }

  // *************************** ICellClickListener ***************************

  override def onCellClicked(cell: Cell): Unit = {
    val oldState = cell.getState
    if (cellSelectionMode == CellSelectionMode.TOGGLE) {
      if (cell.getState == State.ALIVE) {
        cell.setState(State.DEAD)
      }  else {
        cell.setState(State.ALIVE)
      }
    } else {
      cell.setState(State.ALIVE)
    }

    if (cell.getState != oldState) {
      if (isGameReset && cell.getState == State.ALIVE) {
        gameState = GameState.INITIALIZED
      }
      notifyCellStateObservers(cell)
    }

  }

  private def notifyCellStateObservers(cell: Cell): Unit = {
    cellStateObservers.foreach(o => o.onCellStateChange(cell))
  }

  private def notifyGridObservers(): Unit = {
    if (currentCellGrid.isEmpty) return
    var aliveCells = new Array[Cell](0)
    if (gameSettings.playgroundSettings.aliveCells != null) {
       aliveCells = gameSettings.playgroundSettings.aliveCells.map(t => currentCellGrid.get(t._1)(t._2))
    }

    gridStateObservers.foreach(o => o.onGridStructureChange(currentCellGrid.get, aliveCells))
  }

  // *************************** ICellStateProvider ***************************

  override def addCellStateObserver(cellStateObserver: ICellStateObserver): Unit = {
    cellStateObservers.add(cellStateObserver)
  }

  override def removeCellStateObserver(cellStateObserver: ICellStateObserver): Unit = {
    cellStateObservers.remove(cellStateObserver)
  }

  // *************************** IFieldStateObserver ***************************

  override def onFieldUpdated(nextField: Array[Array[Cell]]): Unit = {
    cellStateObservers.foreach(o => o.onCellsStateChange(nextField))
  }

  override def addGridStateObserver(gridStateObserver: IGridStateObserver): Unit = {
    gridStateObservers.add(gridStateObserver)
  }

  override def removeGridStateObserver(gridStateObserver: IGridStateObserver): Unit = {
    gridStateObservers.remove(gridStateObserver)
  }

  override def applyImpactScores(impactScores: ImpactScores): Unit = {
    fieldUpdater.updateImpactScore(impactScores)
  }

  override def applyLifeScores(lifeScores: LifeScores): Unit = {
    gameSettings.lifeScores = lifeScores
    fieldUpdater.updateLifeScores(lifeScores)
  }

  override def applyPlaygroundSettings(playgroundSettings: PlaygroundSettings): Unit = {
    synchronized {
      if (isGameRunning) {
        pause()
      }

      setPlaygroundSettings(playgroundSettings)
      setGridController(new HexagonalGridController(playgroundSettings.borderSize))

      _initGame(true)

      if (isGamePaused) {
        start()
      }
    }
  }

  private def deepCopyGrid(source: Array[Array[Cell]], destination: Array[Array[Cell]]): Unit = {
    val aliveCells = new ArrayBuffer[(Int, Int)]()
    for {
      x <- 0 until Math.min(source.length, destination.length)
      y <- 0 until Math.min(source(0).length, destination(0).length)
    } {
      if (source(x)(y).getState == State.ALIVE) {
        aliveCells.append((x, y))
        destination(x)(y).setState(State.ALIVE)
      }
    }

    gameSettings.playgroundSettings.aliveCells = aliveCells.toArray
  }

  override def initGame(): Unit = {
    _initGame()
  }

  private def _initGame(onApplySettings: Boolean = false): Unit = {
    if (gameState == GameState.UNINITIALIZED) {
      gameState = GameState.INITIALIZED
    }

    val nextCellGrid = gridController.get.generateGrid(
      gameSettings.playgroundSettings.gridWidth, gameSettings.playgroundSettings.gridHeight
    )

    if (onApplySettings && currentCellGrid.nonEmpty) {
      deepCopyGrid(currentCellGrid.get, nextCellGrid)
    }
    currentCellGrid = Some(nextCellGrid)

    if (!onApplySettings) {
      setAliveCells()
    }
    fieldUpdater.setMainField(currentCellGrid.get)
    fieldUpdater.setGridController(gridController.get)
    notifyGridObservers()
  }

}
