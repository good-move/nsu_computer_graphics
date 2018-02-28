package ru.nsu.fit.g15201.boltava.view.toolbar

import javafx.fxml.FXML
import javafx.scene.control.{ScrollPane, ToggleButton, ToolBar}
import javafx.scene.image.ImageView
import javafx.scene.input.MouseEvent
import javafx.scene.layout.VBox
import javafx.stage.FileChooser.ExtensionFilter
import javafx.stage.{FileChooser, Window}

import ru.nsu.fit.g15201.boltava.model.logic.ConfigManager
import ru.nsu.fit.g15201.boltava.view.AlertHelper
import ru.nsu.fit.g15201.boltava.view.about.AboutDialog
import ru.nsu.fit.g15201.boltava.view.help.HelpDialog
import ru.nsu.fit.g15201.boltava.view.toolbar.IContract.CellSelectionMode.CellSelectionMode
import ru.nsu.fit.g15201.boltava.view.toolbar.IContract.{CellSelectionMode, IPresenter, IView}


class ToolbarView extends IView {

  private var presenter: IPresenter = _

  @FXML var root: VBox = _
  @FXML var toolbar: ToolBar = _
  @FXML var gameFieldImageView: ImageView = _
  @FXML var scrollPane: ScrollPane = _
  @FXML var setToggleModeBtn: ToggleButton = _
  @FXML var setReplaceModeBtn: ToggleButton = _

  private var window: Window = _

  @FXML
  def initialize(): Unit = {
//    window = toolbar.getScene.getWindow
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
    val fileChooser = createProperFileChooser("Open Game Model File")
    val file = fileChooser.showOpenDialog(window)
    if (file != null) {
      presenter.onOpenModel(file.getAbsolutePath)
    }
  }

  @FXML
  protected def onSaveModel(event: MouseEvent): Unit = {
    val fileChooser = createProperFileChooser("Select Model File")
    val file = fileChooser.showSaveDialog(window)
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
    val aboutDialog = new AboutDialog(window)
    aboutDialog.show()
  }

  @FXML
  def onHelp(event: MouseEvent): Unit = {
    val helpDialog = new HelpDialog(window)
    helpDialog.show()
  }

  @FXML
  def onOpenSettings(event: MouseEvent): Unit = {
    presenter.onOpenSettings()
  }

  private def createProperFileChooser(title: String): FileChooser = {
    val fileChooser: FileChooser = new FileChooser()
    fileChooser.setTitle(title)
    fileChooser.getExtensionFilters.add(new ExtensionFilter(
      s"${ConfigManager.MODEL_FILE_DESCRIPTION}", s"*.${ConfigManager.MODEL_FILE_EXTENSION}"
    ))
    fileChooser
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