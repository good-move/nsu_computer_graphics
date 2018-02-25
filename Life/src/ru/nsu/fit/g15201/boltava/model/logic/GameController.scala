package ru.nsu.fit.g15201.boltava.model.logic

import java.util.concurrent.{ScheduledFuture, ScheduledThreadPoolExecutor, TimeUnit}

import ru.nsu.fit.g15201.boltava.model.canvas.IGridController
import ru.nsu.fit.g15201.boltava.view.ICellStateObserver

import scala.collection.mutable

/**
  * Controls logic of the whole game.
  *
  */
class GameController extends IGameLogicController with IFieldStateObserver {

  private val executor = new ScheduledThreadPoolExecutor(1)
  private val fieldUpdateInterval = 1000
  private var updateTask: ScheduledFuture[_] = _

  private var gridController: IGridController = _
  private var gridParameters: GridSettings = _

  private var cellGrid: Array[Array[Cell]] = _
  private var cellSelectionMode = CellSelectionMode.TOGGLE

  private var fieldUpdater: ConwayFieldUpdater = _
  private val cellStateObservers = new mutable.HashSet[ICellStateObserver]()

  private var gameState = GameState.UNINITIALIZED

  private val MAX_GRID_SIDE_SIZE = 500
  private val MAX_BORDER_WIDTH = 15
  private val MAX_CELL_SIDE_SIZE = 50

  { // constructor code
    fieldUpdater = new ConwayFieldUpdater
    fieldUpdater.setStateObserver(this)
  }

  override def setGridParams(gridParameters: GridSettings): Unit = {
    validateGridParameters(gridParameters)
    if (gameState == GameState.UNINITIALIZED) {
      gameState = GameState.INITIALIZED
    }
    this.gridParameters = gridParameters
    generateGrid()
    fieldUpdater.setMainField(cellGrid)
  }

  override def getGridParams: GridSettings = this.gridParameters

  private def validateGridParameters(gridParameters: GridSettings) = {
    if (gridParameters.width <= 0 || gridParameters.width > MAX_GRID_SIDE_SIZE ||
      gridParameters.height <= 0 || gridParameters.height > MAX_GRID_SIDE_SIZE) {
      throw new RuntimeException(
        s"Invalid grid dimensions. Grid width and height " +
          s"must be positive integers between 1 and $MAX_GRID_SIDE_SIZE.")
    }

    if (gridParameters.borderWidth <= 0 || gridParameters.borderWidth > MAX_BORDER_WIDTH) {
      throw new RuntimeException(s"Border width must be a positive integer not greater than $MAX_BORDER_WIDTH.")
    }

    if (gridParameters.cellSideSize <= 0 || gridParameters.cellSideSize > MAX_CELL_SIDE_SIZE) {
      throw new RuntimeException(s"Cell side size must be a positive integer not grater than $MAX_CELL_SIDE_SIZE.")
    }

    gridParameters.aliveCells.foreach(cell => {
      if (cell._1 < 0 || cell._1 >= gridParameters.width ||
        cell._2 < 0 || cell._2 >= gridParameters.height) {
        throw new RuntimeException(s"Cell coordinates out of bounds: $cell " +
          s"(width: ${gridParameters.width}, height: ${gridParameters.height}).")
      }
    })

  }

  override def getCells: Array[Array[Cell]] = cellGrid

  override def getGridController: IGridController = gridController

  override def setGridController(gridController: IGridController): Unit = {
    this.gridController = gridController
    fieldUpdater.setGridController(gridController)
  }

  override def setCellSelectionMode(newCellSelectionMode: CellSelectionMode.Value): Unit = {
    cellSelectionMode = newCellSelectionMode
  }

  override def start(): Unit = {
    if (!this.isGameInitialized) {
      throw new RuntimeException("Game Field is not initialized")
    }

    if (this.isGameReset || this.isGameFinished) {
      throw new RuntimeException("Game ")
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

  override def getCellSelectionMode: CellSelectionMode.Value = cellSelectionMode

  // *************************** Private Methods ***************************

  private def generateGrid(): Unit = {
    cellGrid = gridController.generateGrid(gridParameters.width, gridParameters.height)
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

  override def subscribe(cellStateObserver: ICellStateObserver): Unit = {
    cellStateObservers.add(cellStateObserver)
  }

  override def unsubscribe(cellStateObserver: ICellStateObserver): Unit = {
    cellStateObservers.remove(cellStateObserver)
  }

  // *************************** IFieldStateObserver ***************************

  override def onFieldUpdated(nextField: Array[Array[Cell]]): Unit = {
    cellStateObservers.foreach(o => o.onCellsStateChange(nextField))
  }

  override def isGameInitialized: Boolean = gameState != GameState.UNINITIALIZED

  override def isGameRunning: Boolean = gameState == GameState.RUNNING

  override def isGamePaused: Boolean = gameState == GameState.PAUSED

  override def isGameReset: Boolean = gameState == GameState.RESET

  override def isGameFinished: Boolean = gameState == GameState.FINISHED
}
