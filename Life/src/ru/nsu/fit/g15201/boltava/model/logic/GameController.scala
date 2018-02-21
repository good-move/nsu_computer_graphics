package ru.nsu.fit.g15201.boltava.model.logic

import java.util.concurrent.{ScheduledFuture, ScheduledThreadPoolExecutor, TimeUnit}

import ru.nsu.fit.g15201.boltava.model.canvas.IGridController
import ru.nsu.fit.g15201.boltava.view.ICellStateObserver

import scala.collection.mutable

/**
  * Controls logic of the whole game.
  *
  * @param gridController
  */
class GameController(private val gridController: IGridController) extends IGameLogicController with IFieldStateObserver {

  private val executor = new ScheduledThreadPoolExecutor(1)
  private val fieldUpdateInterval = 1000
  private var updateTask: ScheduledFuture[_] = _

  private var gridParameters: GridParameters = _
  private var cellGrid: Array[Array[Cell]] = _
  private var cellSelectionMode = CellSelectionMode.REPLACE

  private var fieldUpdater: ConwayFieldUpdater = _
  private val cellStateObservers = new mutable.HashSet[ICellStateObserver]()

  private var gameState = GameState.UNINITIALIZED

  { // constructor code
    fieldUpdater = new ConwayFieldUpdater(gridController)
    fieldUpdater.setStateObserver(this)
  }

  override def setGridParams(gridParameters: GridParameters): Unit = {
    if (gameState == GameState.UNINITIALIZED) {
      gameState = GameState.INITIALIZED
    }
    this.gridParameters = gridParameters
    generateGrid()
    fieldUpdater.setMainField(cellGrid)
  }

  override def getCells: Array[Array[Cell]] = cellGrid

  override def getGridController: IGridController = gridController

  override def setCellSelectionMode(newCellSelectionMode: CellSelectionMode.Value): Unit = {
    cellSelectionMode = newCellSelectionMode
  }

  override def start(): Unit = {
    if (!this.isGameModelSet) {
      throw new RuntimeException("Game Field is not initialized")
    }

    stopUpdater()
    updateTask = executor.scheduleAtFixedRate(fieldUpdater, 0, fieldUpdateInterval, TimeUnit.MILLISECONDS)
    gameState = GameState.STARTED
  }

  override def pause(): Unit = {
    stopUpdater()
  }

  override def reset(): Unit = {
    stopUpdater()

    cellGrid.foreach(_.foreach(cell => {
      val oldState = cell.getState
      cell.setState(State.DEAD)
      if (oldState != cell.getState) {
        notifyCellStateObservers(cell)
      }
    }))
    fieldUpdater.setMainField(cellGrid)

    gameState = GameState.RESET
  }

  override def nextStep(): Unit = {
    if (!this.isGameModelSet) {
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

  override def isGameStarted: Boolean = gameState == GameState.STARTED

  override def isGameFinished: Boolean = gameState == GameState.FINISHED

  override def isGameModelSet: Boolean = gameState != GameState.UNINITIALIZED

  private object GameState extends Enumeration {
    type GameState = Value
    val UNINITIALIZED, INITIALIZED, STARTED, PAUSED, RESET, FINISHED = Value
  }

}
