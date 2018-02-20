package ru.nsu.fit.g15201.boltava.model.logic

import java.util.concurrent.{ScheduledFuture, ScheduledThreadPoolExecutor, TimeUnit}

import ru.nsu.fit.g15201.boltava.model.canvas.IGridController
import ru.nsu.fit.g15201.boltava.view.ICellStateObserver

import scala.collection.mutable


class GameController(private val fieldWidth: Int = 10,
                     private val fieldHeight: Int = 10,
                     private val gridController: IGridController)
                     extends IGameLogicController with IFieldStateObserver {

  private val executor = new ScheduledThreadPoolExecutor(1)
  private val fieldUpdateInterval = 1000
  private var updateTask: ScheduledFuture[_] = _

  private var cellGrid: Array[Array[Cell]] = _
  private var cellSelectionMode = CellSelectionMode.REPLACE

  private var fieldUpdater: ConwayFieldUpdater = _
  private val cellStateObservers = new mutable.HashSet[ICellStateObserver]()

  { // constructor code
    generateGrid()
    fieldUpdater = new ConwayFieldUpdater(cellGrid, gridController)
    fieldUpdater.setStateObserver(this)
  }


  override def getCells: Array[Array[Cell]] = cellGrid

  override def getGridController: IGridController = gridController

  override def setCellSelectionMode(newCellSelectionMode: CellSelectionMode.Value): Unit = {
    cellSelectionMode = newCellSelectionMode
  }

  override def start(): Unit = {
    println("Game Started!")
    if (updateTask != null) {
      updateTask.cancel({
        val mayInterrupt = true; mayInterrupt
      })
    }
    fieldUpdater.setInitialField(cellGrid)
    updateTask = executor.scheduleAtFixedRate(fieldUpdater, 0, fieldUpdateInterval, TimeUnit.MILLISECONDS)
  }

  override def pause(): Unit = {

  }

  override def reset(): Unit = {
    cellGrid.foreach(_.foreach(cell => {
      val oldState = cell.getState
      cell.setState(State.DEAD)
      if (oldState != cell.getState) {
        notifyCellStateObservers(cell)
      }
    }))
    fieldUpdater.setInitialField(cellGrid)
  }

  override def nextStep(): Unit = {
    fieldUpdater.run()
  }

  override def getCellSelectionMode: CellSelectionMode.Value = cellSelectionMode

  // *************************** Private Methods ***************************

  private def generateGrid(): Unit = {
    cellGrid = gridController.generateGrid(fieldWidth, fieldHeight)
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
    println("Updating field")
    cellStateObservers.foreach(o => o.onCellsStateChange(nextField))
  }

}

object CellSelectionMode extends Enumeration {
  type CellSelectionMode = Value
  val TOGGLE, REPLACE = Value
}
