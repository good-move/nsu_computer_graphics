package ru.nsu.fit.g15201.boltava.presentation_layer.settings

import javafx.stage.Window

import ru.nsu.fit.g15201.boltava.presentation_layer.CustomModalDialog
import ru.nsu.fit.g15201.boltava.presentation_layer.settings.IContract.IView

import scala.reflect.io.Path

class SettingsPane(owner: Window)
      extends CustomModalDialog[SettingsPaneView, Unit](
        "Settings", SettingsPane.cssPath, SettingsPane.contentPath, owner
      ) {}

object SettingsPane {
  // todo: fix cssPath
  private val cssPath = Path("styles/about_modal.css").toString()
  private val contentPath = getClass.getResource("./settings_view.fxml")

  def display(owner: Window): IView = {
    val settingsPane = new SettingsPane(owner)
    settingsPane.showAndWait()
    settingsPane.getController
  }
}