package ru.nsu.fit.g15201.boltava.presentation_layer.toolbar

import ru.nsu.fit.g15201.boltava.presentation_layer.toolbar.Contract.{IToolbarPresenter, IToolbarView}

import scalafx.stage.FileChooser

class ToolbarPresenter(view: IToolbarView) extends IToolbarPresenter {

  {
    view.setPresenter(this)
  }

  override def beforeChooseFile(filePath: String): FileChooser = ???

  override def onImageOpened(imagePath: String): Unit = ???

  override def onSaveImage(imagePath: String): Unit = ???

}
