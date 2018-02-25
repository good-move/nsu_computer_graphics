package ru.nsu.fit.g15201.boltava.view.settings

import javafx.stage.Window

import ru.nsu.fit.g15201.boltava.view.CustomModalDialog

import scala.reflect.io.Path

class SettingsPane(owner: Window = null)
      extends CustomModalDialog[SettingsPaneController, Unit](
        "Settings", SettingsPane.cssPath, SettingsPane.contentPath
      ) {
  {
    initOwner(owner)
  }
}

object SettingsPane {
  // todo: fix cssPath
  private val cssPath = Path("styles/about_modal.css").toString()
  private val contentPath = getClass.getResource("./settings_view.fxml")
}