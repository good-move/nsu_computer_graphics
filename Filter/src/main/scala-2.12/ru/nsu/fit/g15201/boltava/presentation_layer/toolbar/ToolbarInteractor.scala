package ru.nsu.fit.g15201.boltava.presentation_layer.toolbar

import javafx.scene.image.PixelFormat
import ru.nsu.fit.g15201.boltava.domain_layer.exception.ImageInitializationError
import ru.nsu.fit.g15201.boltava.domain_layer.settings.{FileExtension, ImageProperties}
import ru.nsu.fit.g15201.boltava.domain_layer.storage.ImageHolder
import ru.nsu.fit.g15201.boltava.domain_layer.filter.RawImage
import ru.nsu.fit.g15201.boltava.presentation_layer.toolbar.Contract.IToolbarInteractor
import scalafx.scene.image.Image

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

class ToolbarInteractor extends IToolbarInteractor {

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

}

