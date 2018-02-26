package ru.nsu.fit.g15201.boltava.model.logic

import ru.nsu.fit.g15201.boltava.model.canvas.IGridController
import ru.nsu.fit.g15201.boltava.model.logic.CellSelectionMode.CellSelectionMode
import ru.nsu.fit.g15201.boltava.model.logic.GameState.GameState

trait IGameLogicController extends ICellStateProvider with ICellClickListener {

  def start(): Unit
  def nextStep(): Unit
  def pause(): Unit
  def reset(): Unit

  def setGridParams(gridParameters: GameSettings): Unit
  def getGridParams: GameSettings

  def getBoundsSettings: BoundsSettings

  def setCellSelectionMode(cellSelectionMode: CellSelectionMode)
  def getCellSelectionMode: CellSelectionMode

  def isGameInitialized: Boolean
  def isGameRunning: Boolean
  def isGamePaused: Boolean
  def isGameReset: Boolean
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

object GameState extends Enumeration {
  type GameState = Value
  val UNINITIALIZED, INITIALIZED, RUNNING, PAUSED, RESET, FINISHED = Value
}