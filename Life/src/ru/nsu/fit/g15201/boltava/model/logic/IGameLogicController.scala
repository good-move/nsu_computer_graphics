package ru.nsu.fit.g15201.boltava.model.logic

import ru.nsu.fit.g15201.boltava.model.canvas.IGridController
import ru.nsu.fit.g15201.boltava.model.logic.CellSelectionMode.CellSelectionMode

trait IGameLogicController extends ICellStateProvider with ICellClickListener {

  def start(): Unit
  def nextStep(): Unit
  def pause(): Unit
  def reset(): Unit

  def setGridParams(gridParameters: GridParameters): Unit

  def setCellSelectionMode(cellSelectionMode: CellSelectionMode)
  def getCellSelectionMode: CellSelectionMode

  def isGameModelSet: Boolean
  def isGameStarted: Boolean
  def isGameFinished: Boolean

  // TODO: move these out
  def setGridController(gridController: IGridController)
  def getGridController: IGridController
  def getCells: Array[Array[Cell]]

}

object CellSelectionMode extends Enumeration {
  type CellSelectionMode = Value
  val TOGGLE, REPLACE = Value
}