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
    def applyEmbossFilter(): Unit
    def applySharpenFilter(): Unit
    def applyMedianFilter(neighborsCount: Int): Unit
    def applyContourFilter(): Unit
    def applyWaterColorFilter(neighborsCount: Int): Unit
    def applyImageRotation(angle: Int): Unit

    def getKernelsList: Seq[String]
    def applyEdgeDetectionKernel(kernel: String): Unit

    def getValidImageExtensions: Seq[FileExtension]
    def onImageOpened(image: Image)
  }

  case object KernelType extends Enumeration {
    type KernelType = Value
    val Robert, Prewitt, Sobel = Value
  }

}
