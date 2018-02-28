package ru.nsu.fit.g15201.boltava.view.help

import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.input.MouseEvent
import javafx.stage.Stage

class HelpDialogController {

  @FXML private var closeButton: Button = _

  @FXML
  protected def onClose(event: MouseEvent): Unit = {
    val w: Stage = closeButton.getScene.getWindow.asInstanceOf[Stage]
    w.close()
  }

}
