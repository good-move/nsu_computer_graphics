package ru.nsu.fit.g15201.boltava.presentation_layer.toolbar

import javafx.fxml.FXML
import javafx.scene.control.{ButtonType, ToggleButton, ToolBar}
import javafx.scene.input.MouseEvent
import javafx.stage.{FileChooser, Window}

import ru.nsu.fit.g15201.boltava.presentation_layer.AlertHelper
import ru.nsu.fit.g15201.boltava.presentation_layer.about.AboutDialog
import ru.nsu.fit.g15201.boltava.presentation_layer.help.HelpDialog
import ru.nsu.fit.g15201.boltava.presentation_layer.main.MainActivity
import ru.nsu.fit.g15201.boltava.presentation_layer.toolbar.IContract.CellSelectionMode.CellSelectionMode
import ru.nsu.fit.g15201.boltava.presentation_layer.toolbar.IContract.{CellSelectionMode, IPresenter, IView}


class ToolbarView extends IView {

  private var presenter: IPresenter = _

  @FXML var toolbar: ToolBar = _
  @FXML var setToggleModeBtn: ToggleButton = _
  @FXML var setReplaceModeBtn: ToggleButton = _

  @FXML
  def initialize(): Unit = {
    ToolbarView.toolbarView = this
    MainActivity.getWindow.setOnCloseRequest(_ => {
      presenter.onClose()
    })
  }

  @FXML
  protected def onPlay(event: MouseEvent): Unit = {
    presenter.onPlay()
  }

  @FXML
  protected def onPause(event: MouseEvent): Unit = {
    presenter.onPause()
  }

  @FXML
  protected def onReset(event: MouseEvent): Unit = {
    presenter.onReset()
  }

  @FXML
  protected def onNextStep(event: MouseEvent): Unit = {
    presenter.onNextStep()
  }

  @FXML
  protected def onOpenModel(event: MouseEvent): Unit = {
    presenter.onOpenModel()
  }

  @FXML
  protected def onSaveModel(event: MouseEvent): Unit = {
    presenter.onSaveModel()
  }

  @FXML
  def onSetReplace(event: MouseEvent): Unit = {
    presenter.onSetReplace()
  }

  @FXML
  def onSetToggle(event: MouseEvent): Unit = {
    presenter.onSetToggle()
  }

  @FXML
  def onAbout(event: MouseEvent): Unit = {
    val aboutDialog = new AboutDialog(getWindow)
    aboutDialog.show()
  }

  @FXML
  def onHelp(event: MouseEvent): Unit = {
    val helpDialog = new HelpDialog(getWindow)
    helpDialog.show()
  }

  @FXML
  def onOpenSettings(event: MouseEvent): Unit = {
    presenter.onOpenSettings()
  }

  @FXML
  def onToggleImpactScores(event: MouseEvent): Unit = {
    presenter.onToggleImpactScores()
  }

  override def setCellSelectionButton(cellSelectionMode: CellSelectionMode): Unit = {
    cellSelectionMode match {
      case CellSelectionMode.TOGGLE =>
        setToggleModeBtn.setSelected(true)
        setReplaceModeBtn.setSelected(false)

      case CellSelectionMode.REPLACE =>
        setReplaceModeBtn.setSelected(true)
        setToggleModeBtn.setSelected(false)

      case CellSelectionMode.NONE =>
        setReplaceModeBtn.setSelected(false)
        setToggleModeBtn.setSelected(false)
    }
  }

  override def showError(title: String, message: String): Unit = {
    AlertHelper.showError(getWindow, title, message)
  }

  override def showWarning(title: String, message: String): Unit = {
    AlertHelper.showWarning(getWindow, title, message)
  }

  override def showInfo(title: String, message: String): Unit = {
    AlertHelper.showInformation(getWindow, title, message)
  }

  override def setPresenter(presenter: IPresenter): Unit = this.presenter = presenter

  override def showOfferSaveModel(): Unit = {
    val result = AlertHelper.showConfirmation(getWindow, "Playground is modified", "Do you want to save playground model?")
    if (result == ButtonType.OK) {
      presenter.onAgreeSaveModel()
    }
  }

  private def getWindow: Window = {
    toolbar.getScene.getWindow
  }

  override def showSaveFileChooser(fileChooser: FileChooser, onFileChosen: String => Unit): Unit = {
    val file = fileChooser.showSaveDialog(getWindow)
    if (file != null) {
      onFileChosen(file.getAbsolutePath)
    }
  }

  override def showOpenFileChooser(fileChooser: FileChooser, onFileChosen: String => Unit): Unit = {
    val file = fileChooser.showOpenDialog(getWindow)
    if (file != null) {
      onFileChosen(file.getAbsolutePath)
    }
  }
}

object ToolbarView {
  private var toolbarView: ToolbarView = _

  def getInstance: ToolbarView = toolbarView
}
