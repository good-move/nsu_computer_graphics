package ru.nsu.fit.g15201.boltava.domain_layer.logic

import java.util.concurrent.{ScheduledFuture, ScheduledThreadPoolExecutor, TimeUnit}

import ru.nsu.fit.g15201.boltava.domain_layer.canvas.IGridController
import ru.nsu.fit.g15201.boltava.domain_layer.logic.settings.{GameSettings, SettingsBounds}
import ru.nsu.fit.g15201.boltava.presentation_layer.main.{ICellStateObserver, IGridStateObserver}

import scala.collection.mutable
import scala.util.{Failure, Success, Try}

/**
  * Controls logic of the whole game.
  *
  */
class GameController extends IGameLogicController with IFieldStateObserver {

  private val executor = new ScheduledThreadPoolExecutor(1)
  private val fieldUpdateInterval = 1000
  private var updateTask: ScheduledFuture[_] = _

  private var gridController: IGridController = _
  private var gameSettings = new GameSettings
  private val boundsSettings = new SettingsBounds

  private var cellGrid: Array[Array[Cell]] = _
  private var cellSelectionMode = CellSelectionMode.TOGGLE

  private var fieldUpdater: ConwayFieldUpdater = _
  private val cellStateObservers = new mutable.HashSet[ICellStateObserver]()
  private val gridStateObservers = new mutable.HashSet[IGridStateObserver]()

  private var gameState = GameState.UNINITIALIZED

  private val MAX_GRID_SIDE_SIZE = 500
  private val MAX_BORDER_WIDTH = 15
  private val MAX_CELL_SIDE_SIZE = 50


  { // constructor code
    fieldUpdater = new ConwayFieldUpdater
    fieldUpdater.setStateObserver(this)
    boundsSettings.minBorderSize = 10
    boundsSettings.maxBorderSize = MAX_CELL_SIDE_SIZE
    boundsSettings.minBorderWidth = 1
    boundsSettings.maxBorderWidth = MAX_BORDER_WIDTH
    boundsSettings.maxGridSize - MAX_GRID_SIDE_SIZE
  }

  override def getSettingsBounds: SettingsBounds = boundsSettings

  override def setGridParams(gameSettings: GameSettings): Unit = {
    Try(validateGridParameters(gameSettings)) match {
      case Success(_) => applyGameSettings(gameSettings)
      case Failure(t) => throw t
    }
  }

  override def getGameSettings: GameSettings = this.gameSettings

  private def applyGameSettings(gameSettings: GameSettings): Unit = {
    if (gameState == GameState.UNINITIALIZED) {
      gameState = GameState.INITIALIZED
    }
    this.gameSettings = gameSettings
    generateGrid()
    setAliveCells()
    notifyGridObservers()
    fieldUpdater.setMainField(cellGrid)
  }

  private def validateGridParameters(gridParameters: GameSettings) = {
    if (gridParameters.gridWidth <= 0 || gridParameters.gridWidth > MAX_GRID_SIDE_SIZE ||
      gridParameters.gridHeight <= 0 || gridParameters.gridHeight > MAX_GRID_SIDE_SIZE) {
      throw new RuntimeException(
        s"Invalid grid dimensions. Grid width and height " +
          s"must be positive integers between 1 and $MAX_GRID_SIDE_SIZE.")
    }

    if (gridParameters.borderWidth <= 0 || gridParameters.borderWidth > MAX_BORDER_WIDTH) {
      throw new RuntimeException(s"Border width must be a positive integer not greater than $MAX_BORDER_WIDTH.")
    }

    if (gridParameters.borderSize <= 0 || gridParameters.borderSize > MAX_CELL_SIDE_SIZE) {
      throw new RuntimeException(s"Cell side size must be a positive integer not grater than $MAX_CELL_SIDE_SIZE.")
    }

    gridParameters.aliveCells.foreach(cell => {
      if (cell._1 < 0 || cell._1 >= gridParameters.gridWidth ||
        cell._2 < 0 || cell._2 >= gridParameters.gridHeight) {
        throw new RuntimeException(s"Cell coordinates out of bounds: $cell " +
          s"(width: ${gridParameters.gridWidth}, height: ${gridParameters.gridHeight}).")
      }
    })

  }

  override def getCells: Array[Array[Cell]] = cellGrid

  override def setGridController(gridController: IGridController): Unit = {
    this.gridController = gridController
    fieldUpdater.setGridController(gridController)
  }

  override def getGridController: IGridController = gridController

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

    gameState = GameState.RESET
    stopUpdater()

    cellGrid.foreach(_.foreach(cell => {
      val oldState = cell.getState
      cell.setState(State.DEAD)
      if (oldState != cell.getState) {
        notifyCellStateObservers(cell)
      }
    }))
    fieldUpdater.setMainField(cellGrid)
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

  private def generateGrid(): Unit = {
    cellGrid = gridController.generateGrid(gameSettings.gridWidth, gameSettings.gridHeight)
  }

  private def setAliveCells(): Unit = {
    gameSettings.aliveCells.foreach(coords => cellGrid(coords._1)(coords._2).setState(State.ALIVE))
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

  private def notifyGridObservers(): Unit = {
    val aliveCells = gameSettings.aliveCells.map(t => cellGrid(t._1)(t._2))

    gridStateObservers.foreach(o => o.onGridStructureChange(cellGrid, aliveCells))
  }

}
