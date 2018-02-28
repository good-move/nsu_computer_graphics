package ru.nsu.fit.g15201.boltava.view.settings

import javafx.stage.Window

import ru.nsu.fit.g15201.boltava.model.logic.{BoundsSettings, GameSettings, IGameLogicController}
import ru.nsu.fit.g15201.boltava.view.settings.IContract.{IInteractor, IPresenter}

object SettingsPaneActivity {

  private var settingsPane: SettingsPane = _
  private var gameController: IGameLogicController = _
  private var presenter: IPresenter = _

  def launch(owner: Window, gameController: IGameLogicController): Unit = {
    this.gameController = gameController
    settingsPane = new SettingsPane(owner)
    presenter = new SettingsPanePresenter(settingsPane.getController, new SettingsPaneActivity)
    settingsPane.show()
  }

}

class SettingsPaneActivity extends IInteractor {

  override def getGameSettings: GameSettings = {
    SettingsPaneActivity.gameController.getGameSettings
  }

  override def getBoundsSettings: BoundsSettings = {
    SettingsPaneActivity.gameController.getBoundsSettings
  }

  override def applyGameSettings(gameSettings: GameSettings): Unit = {
//    SettingsPaneActivity.gameController.applyGameSettings(gameSettings)
  }

}