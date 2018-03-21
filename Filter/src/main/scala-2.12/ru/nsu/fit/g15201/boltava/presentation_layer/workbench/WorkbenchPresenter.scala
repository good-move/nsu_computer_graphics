package ru.nsu.fit.g15201.boltava.presentation_layer.workbench

import ru.nsu.fit.g15201.boltava.domain_layer.filter._
import ru.nsu.fit.g15201.boltava.domain_layer.geometry.{Dimensions, DoublePoint, IntPoint}
import ru.nsu.fit.g15201.boltava.domain_layer.storage.{IImageObserver, ImageHolder}
import ru.nsu.fit.g15201.boltava.presentation_layer.workbench.Contract.{IWorkbenchPresenter, IWorkbenchView}
import scalafx.scene.image.{Image, PixelFormat, WritableImage}
import scalafx.stage.Stage


class WorkbenchPresenter(view: IWorkbenchView)(implicit stage: Stage) extends IWorkbenchPresenter with IImageObserver {

  private val imageBoxDimensions = Dimensions(500, 500)
  private var scaleFactor = 1d

  {
    view.setPresenter(this)
    view.setSelectionBoxParameters(100,100)
    ImageHolder.subscribe(this)
  }

  override def onImagePartSelected(topLeft: DoublePoint, bottomRight: DoublePoint): Unit = {
    val image = ImageHolder.getMainImage
    if (image.isDefined) {
      val top = IntPoint(topLeft.x.toInt, topLeft.y.toInt) * scaleFactor.toInt
      val bottom: IntPoint = IntPoint(bottomRight.x.toInt, bottomRight.y.toInt) * scaleFactor.toInt
      if (CropFilter.canCrop(top, bottom, image.get)) {
        val cropped = CropFilter(top, bottom).transform(image.get)
        ImageHolder.setCroppedImage(cropped)
      }
    }
  }

  override def onImageSelectionMoved(topLeft: DoublePoint, bottomRight: DoublePoint): Unit = {
    println("Image selection moved")
  }

  override def getStage: Stage = stage

  override def onMainImageChanged(newImage: RawImage): Unit = {
    val image = fillDisplayImage(scaleToFit(newImage))
    setScaleFactor(newImage)
    view.setSelectionBoxParameters(imageBoxDimensions.width/scaleFactor.toInt,imageBoxDimensions.height/scaleFactor.toInt)
    view.setMainImage(image)
  }

  override def onCroppedImageChanged(newImage: RawImage): Unit = {
    view.setCroppedImage(fillDisplayImage(newImage))
  }

  override def onFilteredImageChanged(newImage: RawImage): Unit = {
    view.setFilteredImage(fillDisplayImage(newImage))
  }

  private def fillDisplayImage(image: RawImage): Image = {
    val displayImage = new WritableImage(image.width, image.height)
    val pixelFormat = PixelFormat.getIntArgbInstance
    displayImage.pixelWriter.setPixels(
      0, 0,
      image.width, image.height, pixelFormat,
      image.content, 0, image.width
    )
    displayImage
  }

  private def setScaleFactor(image: RawImage): Unit = {
    scaleFactor = (image.width.toDouble / imageBoxDimensions.width).max(image.height.toDouble/imageBoxDimensions.height)
  }

  private def scaleToFit(image: RawImage)(implicit ev: RawImage => Transformable): RawImage = {
    val scaled = image.transform(UniformDownscale(imageBoxDimensions)).get
    ImageHolder.setCroppedImage(scaled)
    scaled
  }

  override def onImageReset(): Unit = {
    val empty = new WritableImage(1,1)
    view.setMainImage(empty)
    view.setCroppedImage(empty)
    view.setFilteredImage(empty)
  }
}
