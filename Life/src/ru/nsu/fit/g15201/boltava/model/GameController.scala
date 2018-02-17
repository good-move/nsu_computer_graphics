package ru.nsu.fit.g15201.boltava.model

import ru.nsu.fit.g15201.boltava.model.canvas.IGridController
import ru.nsu.fit.g15201.boltava.model.logic.Cell

class GameController[T <: Cell](private val fieldWidth: Int = 10,
                                private val fieldHeight: Int = 10,
                                private val gridController: IGridController[T]) {

  private var cellGrid: Array[Array[T]] = _

  { // constructor code
    generateGrid()
  }

  def getCells: Array[Array[T]] = cellGrid

  def getGridController: IGridController[T] = gridController

  private def generateGrid(): Unit = {
    cellGrid = gridController.generateGrid(fieldWidth, fieldHeight)
  }

}
