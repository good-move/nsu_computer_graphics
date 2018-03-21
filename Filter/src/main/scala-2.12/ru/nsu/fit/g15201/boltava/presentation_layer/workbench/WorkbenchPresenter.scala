package ru.nsu.fit.g15201.boltava.presentation_layer.workbench

import ru.nsu.fit.g15201.boltava.domain_layer.filter._
import ru.nsu.fit.g15201.boltava.domain_layer.geometry.{Dimensions, DoublePoint}
import ru.nsu.fit.g15201.boltava.domain_layer.storage.{IImageObserver, ImageHolder}
import ru.nsu.fit.g15201.boltava.presentation_layer.workbench.Contract.{IWorkbenchPresenter, IWorkbenchView}
import scalafx.scene.image.{Image, PixelFormat, WritableImage}
import scalafx.stage.Stage


class WorkbenchPresenter(view: IWorkbenchView)(implicit stage: Stage) extends IWorkbenchPresenter with IImageObserver {

  private val mainImageDimensions = Dimensions(500, 500)

  {
    view.setPresenter(this)
    ImageHolder.subscribe(this)
  }

  override def onImagePartSelected(topLeft: DoublePoint, bottomRight: DoublePoint): Unit = ???

  override def onImageSelectionMoved(topLeft: DoublePoint, bottomRight: DoublePoint): Unit = ???

  override def getStage: Stage = stage

  override def onMainImageChanged(newImage: RawImage): Unit = {
    val image = fillDisplayImage(scaleToFit(newImage))
    view.setMainImage(image)
  }

  override def onCroppedImageChanged(newImage: RawImage): Unit = {
    view.setCroppedImage(fillDisplayImage(newImage))
  }

  override def onFilteredImageChanged(newImage: RawImage): Unit = {
    view.setFilteredImage(fillDisplayImage(newImage))
  }

  private def fillDisplayImage(image: RawImage): Image = {
    val startTime = System.nanoTime()
    val displayImage = new WritableImage(image.width, image.height)
    val pixelFormat = PixelFormat.getIntArgbInstance
    displayImage.pixelWriter.setPixels(
      0, 0,
      image.width, image.height, pixelFormat,
      image.content, 0, image.width
    )
    val endTime = System.nanoTime()
    println(s"Filling image. Time: ${endTime - startTime}")

    displayImage
  }

  def scaleToFit(image: RawImage)(implicit ev: RawImage => Transformable): RawImage = {
    val scaled = image.transform(UniformDownscale(mainImageDimensions)).get
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
