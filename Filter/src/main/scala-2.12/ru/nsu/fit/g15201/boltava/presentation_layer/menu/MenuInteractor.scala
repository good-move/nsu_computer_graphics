package ru.nsu.fit.g15201.boltava.presentation_layer.menu

import javafx.scene.image.PixelFormat
import ru.nsu.fit.g15201.boltava.domain_layer.exception.ImageInitializationError
import ru.nsu.fit.g15201.boltava.domain_layer.filter._
import ru.nsu.fit.g15201.boltava.domain_layer.filter.edge_detection.{EdgeDetectionKernel, PrewittKernel, RobertKernel, SobelKernel}
import ru.nsu.fit.g15201.boltava.domain_layer.settings.{FileExtension, ImageProperties}
import ru.nsu.fit.g15201.boltava.domain_layer.storage.ImageHolder
import ru.nsu.fit.g15201.boltava.presentation_layer.menu.Contract.{IMenuInteractor, KernelType}
import scalafx.scene.image.Image

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

class MenuInteractor extends IMenuInteractor {

  override def getValidImageExtensions: Seq[FileExtension] = ImageProperties.allowedExtensions

  override def onImageOpened(image: Image): Unit = {
    if (image.pixelReader.isDefined) {
      Future {
        RawImage(image.width.toInt, image.height.toInt, loadImageContent(image))
      } onComplete {
        case Success(rawImage) => ImageHolder.setMainImage(rawImage)
        case Failure(t) => throw ImageInitializationError(t.getMessage)
      }
    } else {
      throw UninitializedFieldError("PixelReader is not initialized")
    }
  }

  def loadImageContent(image: Image): Array[Int] = {
    val pixelFormat = PixelFormat.getIntArgbInstance
    val imageWidth = image.width.toInt
    val imageHeight = image.height.toInt
    val imageContent = Array.ofDim[Int](imageWidth * imageHeight)
    val bufferOffset = 0
    val imageXOffset = 0
    val imageYOffset = 0
    image.pixelReader.get.getPixels(
      imageXOffset, imageYOffset,
      imageWidth, imageHeight, pixelFormat,
      imageContent, bufferOffset, imageWidth
    )
    imageContent
  }

  override def applyDoubleUpscale(): Unit = {
    setFilteredImage(DoubleUpscale)
  }

  override def applyGrayScaleFilter(): Unit = {
    setFilteredImage(GrayScaleFilter)
  }

  override def applyNegativeFilter(): Unit = {
    setFilteredImage(NegateFilter)
  }

  def setFilteredImage(transformer: Transformer)(implicit ev: RawImage=>Transformable): Unit = {
    val image = ImageHolder.getCroppedImage
    if (image.isDefined) {
      ImageHolder.setFilteredImage(image.get.transform(transformer).get)
    } else {
      throw new IllegalStateException("Cannot apply filter: cropped image is not set")
    }
  }

  override def canApplyFilter: Boolean = ImageHolder.getCroppedImage.isDefined

  override def getKernelsList: Seq[String] = KernelType.values.toSeq.map(_.toString)

  override def applyEdgeDetectionKernel(kernelString: String): Unit = {
    val kernel = KernelType.values.zip(getKernelsList).find(e => e._2 == kernelString).get._1
    kernel match {
      case KernelType.Sobel => apply(SobelKernel)
      case KernelType.Prewitt => apply(PrewittKernel)
      case KernelType.Robert => apply(RobertKernel)
    }
  }

  private def apply(kernel: EdgeDetectionKernel): Unit = {
    ImageHolder.setFilteredImage(EdgeSelectionFilter(kernel).transform(ImageHolder.getCroppedImage.get))
  }
}

