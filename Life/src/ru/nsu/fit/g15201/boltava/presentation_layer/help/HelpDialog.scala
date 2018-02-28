package ru.nsu.fit.g15201.boltava.presentation_layer.help

import javafx.fxml.{FXML, FXMLLoader}
import javafx.scene.input.MouseEvent
import javafx.scene.{Parent, Scene}
import javafx.stage.{Modality, Stage, Window}

import ru.nsu.fit.g15201.boltava.presentation_layer.CustomModalDialog

import scala.reflect.io.Path

class HelpDialog(owner: Window = null)
      extends CustomModalDialog[HelpDialogController, Unit](
        HelpDialog.title, HelpDialog.cssPath, HelpDialog.contentPath
      ) {

  {
    initOwner(owner)
  }

}

object HelpDialog {
  private val title = "Help"
  private val cssPath = Path("styles/help_modal.css").toString()
  private val contentPath = getClass.getResource("./help_modal_dialog.fxml")

}