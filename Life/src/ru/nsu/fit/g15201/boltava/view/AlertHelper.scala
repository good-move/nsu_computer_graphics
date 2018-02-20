package ru.nsu.fit.g15201.boltava.view

import javafx.scene.control.Alert
import javafx.stage.Window

object AlertHelper {

  def showWarning(owner: Window, title: String, message: String): Unit = {
    showAlert(Alert.AlertType.WARNING, owner, title, message)
  }

  def showError(owner: Window, title: String, message: String): Unit = {
    showAlert(Alert.AlertType.ERROR, owner, title, message)
  }

  def showConfirmation(owner: Window, title: String, message: String): Unit = {
    showAlert(Alert.AlertType.CONFIRMATION, owner, title, message)
  }

  def showAlert(alertType: Alert.AlertType, owner: Window, title: String, message: String) {
    val alert = new Alert(alertType)
    alert.setTitle(title)
    alert.setHeaderText(null)
    alert.setContentText(message)
    alert.initOwner(owner)
    alert.show()
  }
}
