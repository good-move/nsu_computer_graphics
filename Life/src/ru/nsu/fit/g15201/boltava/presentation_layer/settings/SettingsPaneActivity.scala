package ru.nsu.fit.g15201.boltava.presentation_layer.settings

import javafx.stage.Window

import ru.nsu.fit.g15201.boltava.domain_layer.logic.settings._
import ru.nsu.fit.g15201.boltava.presentation_layer.settings.IContract.IPresenter

object SettingsPaneActivity {

  private var settingsPane: SettingsPane = _
  private var gameController: ISettingsController = _
  private var presenter: IPresenter = _

  def launch(owner: Window, settingsController: ISettingsController): Unit = {
    this.gameController = settingsController
    settingsPane = new SettingsPane(owner)
    presenter = new SettingsPanePresenter(settingsPane.getController, new SettingsPaneInteractor(settingsController))
    settingsPane.show()
  }

}
