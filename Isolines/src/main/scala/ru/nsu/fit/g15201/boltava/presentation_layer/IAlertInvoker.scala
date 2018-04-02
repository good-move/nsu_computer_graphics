package ru.nsu.fit.g15201.boltava.presentation_layer

trait IAlertInvoker {

  def showError(title: String, message: String)
  def showWarning(title: String, message: String)
  def showInformation(title: String, message: String)
  def showConfirmation(title: String, message: String)

}
