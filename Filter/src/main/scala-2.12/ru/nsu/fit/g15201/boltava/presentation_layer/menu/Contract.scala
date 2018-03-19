package ru.nsu.fit.g15201.boltava.presentation_layer.menu

import java.io.File

import ru.nsu.fit.g15201.boltava.domain_layer.settings.FileExtension
import ru.nsu.fit.g15201.boltava.presentation_layer.base.IBasePresenter
import scalafx.scene.image.Image

object Contract {

  type FileChooserCallback = File => Unit

  trait IMenuPresenter extends IBasePresenter {

    def onOpenImage()
    def onSaveImage()
    def onExit()

    // ****************** Filters ******************
    def onNegativeChosen()
    def onGrayScaleChosen()
    def onEdgeDetectionChosen()

    // ****************** Transformations ******************
    def onDoubleUpscale()

  }

  trait IMenuInteractor {

    def canApplyFilter: Boolean

    def applyDoubleUpscale(): Unit
    def applyGrayScaleFilter(): Unit
    def applyNegativeFilter(): Unit

    def getValidImageExtensions: Seq[FileExtension]
    def onImageOpened(image: Image)
  }


}
