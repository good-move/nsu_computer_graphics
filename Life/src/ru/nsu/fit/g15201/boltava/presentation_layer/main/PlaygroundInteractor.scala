package ru.nsu.fit.g15201.boltava.view.main

import ru.nsu.fit.g15201.boltava.model.canvas.geometry.{DoublePoint, Point}
import ru.nsu.fit.g15201.boltava.model.logic.{Cell, IGameLogicController}
import ru.nsu.fit.g15201.boltava.view.main.IContract.IInteractor

class PlaygroundInteractor(private val gameController: IGameLogicController) extends IInteractor {

  private var lastDraggedOverCell: Point = (-1, -1)


  override def onFieldClick(point: DoublePoint): Unit = {
    val cellCoords = gameController.getGridController.getCellByPoint(point)
    val cellGrid = gameController.getCells
    if (cellCoords.x < 0 || cellCoords.y < 0 ||
      cellCoords.x >= cellGrid.length || cellCoords.y >= cellGrid(0).length) return

    val cell = gameController.getCells(cellCoords.x)(cellCoords.y)
    gameController.onCellClicked(cell)
  }

  override def shouldUpdateDraggedCell(point: DoublePoint): Boolean = {
    val cellCoords = gameController.getGridController.getCellByPoint(point)
    if (cellCoords.equals(lastDraggedOverCell)) {
      return false
    }

    lastDraggedOverCell = cellCoords
    true
  }

  override def addCellStateObserver(cellStateObserver: ICellStateObserver): Unit = {
    gameController.addCellStateObserver(cellStateObserver)
  }

  override def removeCellStateObserver(cellStateObserver: ICellStateObserver): Unit = {
    gameController.removeCellStateObserver(cellStateObserver)
  }

  override def getGridPixelSize(width: Int, height: Int): (Int, Int) = {
    val (fieldWidth, fieldHeight) = gameController.getGridController.getCartesianFieldSize(width, height)
    (fieldWidth.ceil.toInt, fieldHeight.ceil.toInt)
  }

  override def addGridStateObserver(gridStateObserver: IGridStateObserver): Unit = {
    gameController.addGridStateObserver(gridStateObserver)
  }

  override def removeGridStateObserver(gridStateObserver: IGridStateObserver): Unit = {
    gameController.removeGridStateObserver(gridStateObserver)
  }

}
