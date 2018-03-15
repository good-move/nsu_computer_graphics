package ru.nsu.fit.g15201.boltava.presentation_layer.toolbar

import ru.nsu.fit.g15201.boltava.presentation_layer.base.{IBasePresenter, IBaseView}

import scalafx.stage.FileChooser

object Contract {

  trait IToolbarPresenter extends IBasePresenter {

    def beforeChooseFile(filePath: String): FileChooser
    def onImageOpened(imagePath: String): Unit
    def onSaveImage(imagePath: String): Unit

  }

  trait IToolbarView extends IBaseView[IToolbarPresenter] {

  }

}
