package ru.nsu.fit.g15201.boltava.presentation_layer.toolbar

import ru.nsu.fit.g15201.boltava.presentation_layer.toolbar.Contract.{IToolbarInteractor, IToolbarPresenter, IToolbarView}

import scalafx.stage.FileChooser.ExtensionFilter
import scalafx.stage.{FileChooser, Window}

class ToolbarPresenter(private val view: IToolbarView,
                       private val interactor: IToolbarInteractor
                      )(implicit window: Window) extends IToolbarPresenter {

  {
    view.setPresenter(this)
  }

  override def onOpenImage(imagePath: String): Unit = {
    view.showSaveFile(createFileChooser("Open image")) { path =>
      println(s"Opening $path")
    }
  }

  override def onSaveImage(imagePath: String): Unit = {
    view.showSaveFile(createFileChooser("Save image")) { path =>
      println(s"Saving to $path")
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
    fileChooser
  }

  override def getWindow: Window = window

}
