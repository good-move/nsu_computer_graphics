package ru.nsu.fit.g15201.boltava.presentation_layer.toolbar

import javafx.fxml.FXML
import javafx.scene.control.{ToggleButton, ToolBar}
import javafx.scene.input.MouseEvent

import ru.nsu.fit.g15201.boltava.presentation_layer.AlertHelper
import ru.nsu.fit.g15201.boltava.presentation_layer.about.AboutDialog
import ru.nsu.fit.g15201.boltava.presentation_layer.help.HelpDialog
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
    val fileChooser = presenter.getProperFileChooser("Open Game Model File")
    val file = fileChooser.showOpenDialog(toolbar.getScene.getWindow)
    if (file != null) {
      presenter.onOpenModel(file.getAbsolutePath)
    }
  }

  @FXML
  protected def onSaveModel(event: MouseEvent): Unit = {
    val fileChooser = presenter.getProperFileChooser("Select Model File")
    val file = fileChooser.showSaveDialog(toolbar.getScene.getWindow)
    if (file != null) {
      presenter.onSaveModel(file.getAbsolutePath)
    }
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
    val aboutDialog = new AboutDialog(toolbar.getScene.getWindow)
    aboutDialog.show()
  }

  @FXML
  def onHelp(event: MouseEvent): Unit = {
    val helpDialog = new HelpDialog(toolbar.getScene.getWindow)
    helpDialog.show()
  }

  @FXML
  def onOpenSettings(event: MouseEvent): Unit = {
    presenter.onOpenSettings()
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
    AlertHelper.showError(toolbar.getScene.getWindow, title, message)
  }

  override def showWarning(title: String, message: String): Unit = {
    AlertHelper.showWarning(toolbar.getScene.getWindow, title, message)
  }

  override def showInfo(title: String, message: String): Unit = {
    AlertHelper.showInformation(toolbar.getScene.getWindow, title, message)
  }

  override def setPresenter(presenter: IPresenter): Unit = this.presenter = presenter

}

object ToolbarView {
  private var toolbarView: ToolbarView = _

  def getInstance: ToolbarView = toolbarView
}
