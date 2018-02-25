package ru.nsu.fit.g15201.boltava.view.about

import javafx.stage.Window

import ru.nsu.fit.g15201.boltava.view.CustomModalDialog

import scala.reflect.io.Path

class AboutDialog(owner: Window = null)
      extends CustomModalDialog[AboutDialogController](
        "About", AboutDialog.cssPath, AboutDialog.contentPath
      ) {
  {
    initOwner(owner)
  }

}


object AboutDialog {
  private val cssPath = Path("styles/about_modal.css").toString()
  private val contentPath = getClass.getResource("./about_modal_dialog.fxml")
}