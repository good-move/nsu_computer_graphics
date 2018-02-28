package ru.nsu.fit.g15201.boltava.presentation_layer.toolbar

import ru.nsu.fit.g15201.boltava.domain_layer.canvas.HexagonalGridController
import ru.nsu.fit.g15201.boltava.domain_layer.canvas.geometry.Point
import ru.nsu.fit.g15201.boltava.domain_layer.logic._
import ru.nsu.fit.g15201.boltava.domain_layer.logic.settings.ISettingsController
import ru.nsu.fit.g15201.boltava.presentation_layer.main.MainActivity
import ru.nsu.fit.g15201.boltava.presentation_layer.settings.SettingsPaneActivity
import ru.nsu.fit.g15201.boltava.presentation_layer.toolbar.IContract.IInteractor

import scala.collection.mutable.ArrayBuffer

class ToolbarInteractor(private val gameController: IGameLogicController, private val settingsController: ISettingsController) extends IInteractor {

  override def onSaveModel(path: Option[String]): Unit = {
    var configPath = path
    if (configPath.isEmpty) {
      val lastOpenedPath = ConfigManager.getLastOpenedPath
      if (lastOpenedPath.isDefined) {
        configPath = lastOpenedPath
      } else {
        throw new RuntimeException("No model file found")
      }
    }

    val aliveCells = new ArrayBuffer[Point]()
    gameController.getCells.foreach(_.foreach(cell => {
      if (cell.getState == State.ALIVE) {
        aliveCells.append((cell.getX, cell.getY))
      }
    }))

    ConfigManager.saveGameModel(configPath.get, gameController.getGameSettings.playgroundSettings, aliveCells.toArray)
    gameController.setPlaygroundModified(isModified = false)
  }

  override def onOpenModel(path: String): Unit = {
    val gridParameters = ConfigManager.openGameModel(path)
    gameController.setGridController(new HexagonalGridController(gridParameters.borderSize))
    gameController.setPlaygroundSettings(gridParameters)
    gameController.initGame()
  }

  override def onSetReplace(): Unit = {
    gameController.setCellSelectionMode(CellSelectionMode.REPLACE)
  }

  override def onSetToggle(): Unit = {
    gameController.setCellSelectionMode(CellSelectionMode.TOGGLE)
  }

  override def onOpenSettings(): Unit = {
    // todo: fix weird stuff (view inside model)!
    SettingsPaneActivity.launch(MainActivity.getWindow, settingsController)
  }

  override def getGameController: IGameLogicController = gameController

  override def shouldSavePlaygroundState(): Boolean = {
    gameController.getPlaygroundModified
  }
}

