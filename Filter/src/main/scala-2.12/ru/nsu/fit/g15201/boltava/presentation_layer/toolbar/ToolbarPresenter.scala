package ru.nsu.fit.g15201.boltava.presentation_layer.toolbar

import java.io.File

import ru.nsu.fit.g15201.boltava.presentation_layer.toolbar.Contract.{IToolbarInteractor, IToolbarPresenter, IToolbarView}
import scalafx.scene.image.Image
import scalafx.stage.FileChooser.ExtensionFilter
import scalafx.stage.{FileChooser, Window}


class ToolbarPresenter(private val view: IToolbarView,
                       private val interactor: IToolbarInteractor
                      )(implicit window: Window) extends IToolbarPresenter {

  {
    view.setPresenter(this)
  }

  private def fileToUri(file: File) = s"${file.toURI.toString}"

  override def onOpenImage(): Unit = {
    view.showOpenFile(createFileChooser("Open image")) { file =>
      val uri = fileToUri(file)
      println(s"Opening $uri")
      interactor.onImageOpened(new Image(uri))
    }
  }

  override def onSaveImage(): Unit = {
    view.showSaveFile(createFileChooser("Save image")) { file =>
      val uri = fileToUri(file)
      println(s"Saving to $uri")
    }
  }

  private def createFileChooser(title: String): FileChooser = {
    val fileChooser = new FileChooser()
    fileChooser.setTitle(title)
    interactor.getValidImageExtensions.foreach { extension =>
      fileChooser.extensionFilters.add(
        new ExtensionFilter(extension.extension.toUpperCase , s"*.${extension.extension}")
      )
    }
    fileChooser.initialDirectory = new File(".").getAbsoluteFile
    fileChooser
  }

  override def getWindow: Window = window

}
