package ru.nsu.fit.g15201.boltava.view.about

import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.input.MouseEvent
import javafx.stage.Stage

class AboutDialogController {

  @FXML private var closeButton: Button = _

  @FXML
  protected def onClose(event: MouseEvent): Unit = {
    val stage = closeButton.getScene.getWindow.asInstanceOf[Stage]
    stage.close()
  }

}
