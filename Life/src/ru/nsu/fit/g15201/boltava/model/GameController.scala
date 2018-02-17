package ru.nsu.fit.g15201.boltava.model

import ru.nsu.fit.g15201.boltava.model.canvas.{Polygon, IGridController}

class GameController[T <: Polygon](private val fieldWidth: Int = 10,
                                   private val fieldHeight: Int = 10,
                                   private val gridController: IGridController[T]) {

  private var cellGrid: Array[Array[T]] = _
  private var cellSelectionMode = CellSelectionMode.REPLACE

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

  abstract object CellSelectionMode extends Enumeration {
    val TOGGLE = Value(0)
    val REPLACE = Value(1)
  }

}
