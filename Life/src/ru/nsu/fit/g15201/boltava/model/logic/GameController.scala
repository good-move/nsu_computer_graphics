package ru.nsu.fit.g15201.boltava.model.logic

import ru.nsu.fit.g15201.boltava.model.canvas.IGridController
import ru.nsu.fit.g15201.boltava.model.canvas.geometry.Polygon
import ru.nsu.fit.g15201.boltava.view.ICellStateObserver

import scala.collection.mutable

class GameController[T <: Cell with Polygon](private val fieldWidth: Int = 10,
                                   private val fieldHeight: Int = 10,
                                   private val gridController: IGridController[T]) extends ICellClickListener with ICellStateProvider {

  private var cellGrid: Array[Array[T]] = _
  private var cellSelectionMode = CellSelectionMode.TOGGLE

  private var cellStateObservers = new mutable.HashSet[ICellStateObserver]()

  { // constructor code
    generateGrid()
  }

  def getCells: Array[Array[T]] = cellGrid

  def getGridController: IGridController[T] = gridController

  def setCellSelectionMode(newCellSelectionMode: CellSelectionMode.Value): Unit = {
    cellSelectionMode = newCellSelectionMode
  }

  def getCellSelectionMode: CellSelectionMode.Value = cellSelectionMode

  // *************************** Private Methods ***************************

  private def generateGrid(): Unit = {
    cellGrid = gridController.generateGrid(fieldWidth, fieldHeight)
  }

  override def onCellClicked(cell: Cell with Polygon): Unit = {
    val oldState = cell.getState
    if (cellSelectionMode == CellSelectionMode.TOGGLE) {
      if (cell.getState == cell.State.ALIVE) {
        cell.setState(cell.State.DEAD)
      }  else {
        cell.setState(cell.State.ALIVE)
      }
    } else {
      cell.setState(cell.State.ALIVE)
    }

    if (cell.getState != oldState) {
      cellStateObservers.foreach(o => o.onCellStateChange(cell))
    }

  }

  override def subscribe(cellStateObserver: ICellStateObserver): Unit = {
    cellStateObservers.add(cellStateObserver)
  }

  override def unsubscribe(cellStateObserver: ICellStateObserver): Unit = {
    cellStateObservers.remove(cellStateObserver)
  }
}

object CellSelectionMode extends Enumeration {
  type CellSelectionMode = Value
  val TOGGLE, REPLACE = Value
}
