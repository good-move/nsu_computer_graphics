package ru.nsu.fit.g15201.boltava.presentation_layer.about

import javafx.stage.Window

import ru.nsu.fit.g15201.boltava.presentation_layer.CustomModalDialog

import scala.reflect.io.Path

class AboutDialog(owner: Window = null)
      extends CustomModalDialog[AboutDialogController, Unit](
        AboutDialog.title, AboutDialog.cssPath, AboutDialog.contentPath
      ) {
  {
    initOwner(owner)
  }

}


object AboutDialog {
  private val title = "About"
  private val cssPath = Path("styles/about_modal.css").toString()
  private val contentPath = getClass.getResource("./about_modal_dialog.fxml")
}