package ru.nsu.fit.g15201.boltava.presentation_layer.menu

import java.io.File

import ru.nsu.fit.g15201.boltava.domain_layer.data.FileExtension
import ru.nsu.fit.g15201.boltava.presentation_layer.AlertHelper
import ru.nsu.fit.g15201.boltava.presentation_layer.about.AboutComponent
import ru.nsu.fit.g15201.boltava.presentation_layer.menu.Contract.{IMenuInteractor, IMenuPresenter}
import ru.nsu.fit.g15201.boltava.presentation_layer.workbench.Contract.ColorMapMode
import scalafx.application.Platform
import scalafx.stage.FileChooser.ExtensionFilter
import scalafx.stage.{FileChooser, Stage}
import scalafxml.core.macros.sfxml

@sfxml
class MenuPresenter(interactor: IMenuInteractor, stage: Stage) extends IMenuPresenter {

  {
    interactor.setPresenter(this)
  }

  override def onOpenModel(): Unit = {
    val extension = interactor.modelFileExtension
    val fileChooser = createFileChooser(extension)
    val file = Some(fileChooser.showOpenDialog(stage))
    if (file.isDefined) {
      interactor.openModel(file.get.getAbsolutePath.toString)
    }
  }

  private def createFileChooser(extension: FileExtension): FileChooser = {
    val fileChooser = new FileChooser()
    fileChooser.setTitle("Choose model file")
    fileChooser.extensionFilters.add(
      new ExtensionFilter("Isoline Model File", s"*.${extension.extension}")
    )
    fileChooser.initialDirectory = new File(".").getAbsoluteFile
    fileChooser
  }

  override def onExit(): Unit = {
    interactor.beforeExit()
    Platform.exit()
  }

  override def onToggleShowGrid(): Unit = {
    interactor.toggleGridDisplay()
  }

  override def onToggleShowIsolines(): Unit = {
    interactor.toggleIsolinesDisplay()
  }

  override def onToggleIntersectionDots(): Unit = {
    interactor.toggleIntersectionsDisplay()
  }

  override def onShowAbout(): Unit = {
    AboutComponent().showInNewWindow()
  }

  override def onShowHelp(): Unit = {
//    HelpComponent().showInNewWindow()
    println("onShowHelp clicked")
  }

  override def showError(title: String, message: String): Unit = {
    AlertHelper.showError(stage, title, message)
  }

  override def showWarning(title: String, message: String): Unit = {
    AlertHelper.showWarning(stage, title, message)
  }

  override def showInformation(title: String, message: String): Unit = {
    AlertHelper.showInformation(stage, title, message)
  }

  override def showConfirmation(title: String, message: String): Unit = {
    AlertHelper.showConfirmation(stage, title, message)
  }

  def onToggleShowColorMap(): Unit = {
    interactor.toggleColorMapDisplay()
  }

  def onShowDiscreteColorMap(): Unit = {
    interactor.showDiscreteColorMap()
  }

  def onShowInterpolationColorMap(): Unit = {
    interactor.showInterpolatedColorMap()
  }

  override def onOpenSettings(): Unit = {
    println("onOpenSettings() invoked")
  }

  override def setColorMapDisplayMode(colorMapMode: ColorMapMode.Value): Unit = {
    // do nothing here
  }

}
