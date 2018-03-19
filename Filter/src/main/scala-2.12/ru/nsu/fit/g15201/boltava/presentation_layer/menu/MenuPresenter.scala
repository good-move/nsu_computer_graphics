package ru.nsu.fit.g15201.boltava.presentation_layer.menu

import java.io.File

import ru.nsu.fit.g15201.boltava.presentation_layer.AlertHelper
import ru.nsu.fit.g15201.boltava.presentation_layer.menu.Contract.{FileChooserCallback, IMenuInteractor, IMenuPresenter}
import scalafx.scene.image.Image
import scalafx.stage.FileChooser.ExtensionFilter
import scalafx.stage.{FileChooser, Stage}
import scalafxml.core.macros.sfxml


@sfxml
class MenuPresenter(stage: Stage, interactor: IMenuInteractor) extends IMenuPresenter {

  private def fileToUri(file: File) = s"${file.toURI.toString}"

  override def onOpenImage(): Unit = {
    showOpenFile(createFileChooser("Open image")) { file =>
      val uri = fileToUri(file)
      println(s"Opening $uri")
      interactor.onImageOpened(new Image(uri))
    }
  }

  override def onSaveImage(): Unit = {
    showSaveFile(createFileChooser("Save image")) { file =>
      val uri = fileToUri(file)
      println(s"Saving to $uri")
    }
  }

  private def createFileChooser(title: String): FileChooser = {
    val fileChooser = new FileChooser()
    fileChooser.setTitle(title)
    fileChooser
      .extensionFilters
      .add(new ExtensionFilter("All", interactor.getValidImageExtensions.map(e => s"*.${e.extension}")))
    interactor
      .getValidImageExtensions
      .foreach { extension =>
        fileChooser.extensionFilters.add(
          new ExtensionFilter(extension.extension.toUpperCase , s"*.${extension.extension}")
        )
      }
    fileChooser.initialDirectory = new File(".").getAbsoluteFile
    fileChooser
  }

  private def showSaveFile(fileChooser: FileChooser)(callback: FileChooserCallback): Unit = {
    val file = fileChooser.showSaveDialog(stage)
    if (file != null) {
      callback(file)
    }
  }

  private def showOpenFile(fileChooser: FileChooser)(callback: FileChooserCallback): Unit = {
    val file = fileChooser.showOpenDialog(stage)
    if (file != null) {
      callback(file)
    }
  }



  override def getStage: Stage = stage

  override def onExit(): Unit = {
//    interactor.beforeExit()
    getStage.close()
  }

  override def onNegativeChosen(): Unit = {
    if (!interactor.canApplyFilter) {
      showNoImageChosenError()
    } else {
      interactor.applyNegativeFilter()
    }
  }

  override def onGrayScaleChosen(): Unit = {
    if (!interactor.canApplyFilter) {
      showNoImageChosenError()
    } else {
      interactor.applyGrayScaleFilter()
    }
  }

  override def onEdgeDetectionChosen(): Unit = {
//    val edgeDetectionKernel = EdgeDetectionActivity.launch()
  }

  override def onDoubleUpscale(): Unit = {
    if (!interactor.canApplyFilter) {
      showNoImageChosenError()
    } else {
      interactor.applyDoubleUpscale()
    }
  }

  private def showNoImageChosenError(): Unit = {
    AlertHelper.showError(
      stage,
      "Cannot apply filter",
      "Choose an image and select and area to modify before applying filters"
    )
  }

}
