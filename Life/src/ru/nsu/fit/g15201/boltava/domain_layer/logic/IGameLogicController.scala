package ru.nsu.fit.g15201.boltava.domain_layer.logic

import ru.nsu.fit.g15201.boltava.domain_layer.canvas.IGridController
import ru.nsu.fit.g15201.boltava.domain_layer.logic.CellSelectionMode.CellSelectionMode
import ru.nsu.fit.g15201.boltava.domain_layer.logic.settings.{GameSettings, ISettingsController, PlaygroundSettings, SettingsBounds}
import ru.nsu.fit.g15201.boltava.presentation_layer.main.IGridStateProvider

trait IGameLogicController extends ICellStateProvider with ICellClickListener with IGridStateProvider with ISettingsController {

  def initGame()

  def start(): Unit
  def nextStep(): Unit
  def pause(): Unit
  def reset(): Unit

  def setPlaygroundSettings(playgroundSettings: PlaygroundSettings): Unit
  def getGameSettings: GameSettings

  def getSettingsBounds: SettingsBounds

  def setCellSelectionMode(cellSelectionMode: CellSelectionMode)
  def getCellSelectionMode: CellSelectionMode

  def isGameInitialized: Boolean
  def isGameRunning: Boolean
  def isGamePaused: Boolean
  def isGameReset: Boolean
  def isGameFinished: Boolean

  def getPlaygroundModified: Boolean
  def setPlaygroundModified(isModified: Boolean): Unit

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