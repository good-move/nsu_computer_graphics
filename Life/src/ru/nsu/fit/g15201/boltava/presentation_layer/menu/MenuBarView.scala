package ru.nsu.fit.g15201.boltava.presentation_layer.menu

import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.MenuBar
import javafx.stage.{FileChooser, Stage, Window}

import ru.nsu.fit.g15201.boltava.presentation_layer.AlertHelper
import ru.nsu.fit.g15201.boltava.presentation_layer.about.AboutDialog
import ru.nsu.fit.g15201.boltava.presentation_layer.help.HelpDialog
import ru.nsu.fit.g15201.boltava.presentation_layer.main.MainActivity
import ru.nsu.fit.g15201.boltava.presentation_layer.toolbar.IContract.CellSelectionMode.CellSelectionMode
import ru.nsu.fit.g15201.boltava.presentation_layer.toolbar.IContract.{IPresenter, IView}

class MenuBarView extends IView {

  private var presenter: IPresenter = _

  @FXML var menuBar: MenuBar = _


  @FXML
  def initialize(): Unit = {
    MenuBarView.menuView = this
  }

  @FXML
  protected def onPlay(event: ActionEvent): Unit = {
    presenter.onPlay()
  }

  @FXML
  protected def onPause(event: ActionEvent): Unit = {
    presenter.onPause()
  }

  @FXML
  protected def onReset(event: ActionEvent): Unit = {
    presenter.onReset()
  }

  @FXML
  protected def onNextStep(event: ActionEvent): Unit = {
    presenter.onNextStep()
  }

  @FXML
  protected def onOpenModel(event: ActionEvent): Unit = {
    presenter.onOpenModel()
  }

  @FXML
  protected def onSaveModel(event: ActionEvent): Unit = {
    presenter.onSaveModel()
  }

  @FXML
  def onSetReplace(event: ActionEvent): Unit = {
    presenter.onSetReplace()
  }

  @FXML
  def onSetToggle(event: ActionEvent): Unit = {
    presenter.onSetToggle()
  }

  @FXML
  def onAbout(event: ActionEvent): Unit = {
    val aboutDialog = new AboutDialog(getWindow)
    aboutDialog.show()
  }

  @FXML
  def onHelp(event: ActionEvent): Unit = {
    val helpDialog = new HelpDialog(getWindow)
    helpDialog.show()
  }

  @FXML
  def onOpenSettings(event: ActionEvent): Unit = {
    presenter.onOpenSettings()
  }

  private def getWindow: Window = {
    menuBar.getScene.getWindow
  }

  override def setCellSelectionButton(cellSelectionMode: CellSelectionMode): Unit = {}

  override def setPresenter(presenter: IPresenter): Unit = this.presenter = presenter

  override def showError(title: String, body: String): Unit = {
    AlertHelper.showError(getWindow, title, body)
  }

  override def showWarning(title: String, body: String): Unit = {
    AlertHelper.showWarning(getWindow, title, body)
  }

  override def showInfo(title: String, body: String): Unit = {
    AlertHelper.showInformation(getWindow, title, body)
  }

  override def showSaveFileChooser(fileChooser: FileChooser, onFileChosen: (String) => Unit): Unit = {
    val file = fileChooser.showSaveDialog(getWindow)
    if (file != null) {
      onFileChosen(file.getAbsolutePath)
    }
  }

  override def showOpenFileChooser(fileChooser: FileChooser, onFileChosen: (String) => Unit): Unit = {
    val file = fileChooser.showOpenDialog(getWindow)
    if (file != null) {
      onFileChosen(file.getAbsolutePath)
    }
  }

  override def showOfferSaveModel(): Unit = {}

  override def close(): Unit = {
    MainActivity.getWindow.asInstanceOf[Stage].close()
  }
}

object MenuBarView {

  private var menuView: MenuBarView = _

  def getInstance: MenuBarView = menuView

}