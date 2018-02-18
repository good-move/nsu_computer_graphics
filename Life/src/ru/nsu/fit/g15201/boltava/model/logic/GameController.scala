package ru.nsu.fit.g15201.boltava.model

import ru.nsu.fit.g15201.boltava.model.canvas.IGridController
import ru.nsu.fit.g15201.boltava.model.canvas.geometry.Polygon
import ru.nsu.fit.g15201.boltava.model.logic.Cell

class GameController[T <: Cell with Polygon](private val fieldWidth: Int = 10,
                                   private val fieldHeight: Int = 10,
                                   private val gridController: IGridController[T]) {

  private var cellGrid: Array[Array[T]] = _
  private var cellSelectionMode = CellSelectionMode.TOGGLE

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

}

object CellSelectionMode extends Enumeration {
  type CellSelectionMode = Value
  val TOGGLE, REPLACE = Value
}
