package ru.nsu.fit.g15201.boltava.presentation_layer.toolbar

import java.io.File

import ru.nsu.fit.g15201.boltava.domain_layer.settings.FileExtension
import ru.nsu.fit.g15201.boltava.presentation_layer.base.{IBasePresenter, IBaseView}
import scalafx.scene.image.Image
import scalafx.stage.FileChooser

object Contract {

  trait IToolbarPresenter extends IBasePresenter {

    def onOpenImage(): Unit
    def onSaveImage(): Unit

  }

  type FileChooserCallback = File => Unit

  trait IToolbarView extends IBaseView[IToolbarPresenter] {


    def showSaveFile(fileChooser: FileChooser)(callback: FileChooserCallback)
    def showOpenFile(fileChooser: FileChooser)(callback: FileChooserCallback)

  }

  trait IToolbarInteractor {

    def getValidImageExtensions: Seq[FileExtension]
    def onImageOpened(image: Image)

  }


}
