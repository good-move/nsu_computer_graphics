package ru.nsu.fit.g15201.boltava.presentation_layer.toolbar

import java.io.File

import ru.nsu.fit.g15201.boltava.domain_layer.settings.FileExtension
import ru.nsu.fit.g15201.boltava.presentation_layer.base.{IBasePresenter, IBaseView}

import scalafx.stage.FileChooser

object Contract {

  trait IToolbarPresenter extends IBasePresenter {

    def onOpenImage(imagePath: String): Unit
    def onSaveImage(imagePath: String): Unit

  }

  trait IToolbarView extends IBaseView[IToolbarPresenter] {

    def showSaveFile(fileChooser: FileChooser)(callback: File => Unit)
    def showOpenFile(fileChooser: FileChooser)(callback: File => Unit)

  }

  trait IToolbarInteractor {

    def getValidImageExtensions: Seq[FileExtension]

  }


}
