package ru.nsu.fit.g15201.boltava.view

import javafx.scene.Node
import javafx.scene.control.Alert
import javafx.scene.layout.Region
import javafx.stage.Window

object AlertHelper {

  def showWarning(owner: Window, title: String, message: String, graphic: Node = null): Unit = {
    showAlert(Alert.AlertType.WARNING, owner, title, message, graphic)
  }

  def showError(owner: Window, title: String, message: String, graphic: Node = null): Unit = {
    showAlert(Alert.AlertType.ERROR, owner, title, message, graphic)
  }

  def showConfirmation(owner: Window, title: String, message: String, graphic: Node = null): Unit = {
    showAlert(Alert.AlertType.CONFIRMATION, owner, title, message, graphic)
  }

  def showInformation(owner: Window, title: String, message: String, graphic: Node = null): Unit = {
    showAlert(Alert.AlertType.INFORMATION, owner, title, message, graphic)
  }

  def showAlert(alertType: Alert.AlertType, owner: Window, title: String, message: String, graphic: Node = null): Unit = {
    val alert = new Alert(alertType)
    alert.setTitle(title)
    alert.setHeaderText(null)
    alert.setContentText(message)
    if (graphic != null) {
      alert.setGraphic(graphic)
    }
    alert.getDialogPane.setMinHeight(Region.USE_PREF_SIZE)
    alert.initOwner(owner)
    alert.show()
  }
}
