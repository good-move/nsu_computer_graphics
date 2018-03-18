package ru.nsu.fit.g15201.boltava.presentation_layer.workbench

import ru.nsu.fit.g15201.boltava.domain_layer.filter._
import ru.nsu.fit.g15201.boltava.domain_layer.geometry.{Dimensions, DoublePoint}
import ru.nsu.fit.g15201.boltava.domain_layer.storage.{IImageObserver, ImageHolder}
import ru.nsu.fit.g15201.boltava.presentation_layer.workbench.Contract.{IWorkbenchPresenter, IWorkbenchView}
import scalafx.scene.image.{Image, PixelFormat, WritableImage}
import scalafx.stage.Window


class WorkbenchPresenter(view: IWorkbenchView)(implicit window: Window) extends IWorkbenchPresenter with IImageObserver {

  {
    view.setPresenter(this)
    ImageHolder.subscribe(this)
  }

  override def onImagePartSelected(topLeft: DoublePoint, bottomRight: DoublePoint): Unit = ???

  override def onImageSelectionMoved(topLeft: DoublePoint, bottomRight: DoublePoint): Unit = ???

  override def getWindow: Window = window

  override def onMainImageChanged(newImage: RawImage): Unit = {
    val image = fillDisplayImage(
      Transformable(newImage).transform(UniformDownscale(Dimensions(350, 350))).get
    )
    view.setMainImage(image)
  }

  override def onCroppedImageChanged(newImage: RawImage): Unit = ???

  override def onFilteredImageChanged(newImage: RawImage): Unit = ???

  private def fillDisplayImage(rawImage: RawImage)(implicit ev: RawImage => Transformable): Image = {
    val displayImage = new WritableImage(rawImage.width, rawImage.height)
    val pixelFormat = PixelFormat.getIntArgbInstance
    displayImage.pixelWriter.setPixels(
      0, 0,
      rawImage.width, rawImage.height, pixelFormat,
      rawImage.content, 0, rawImage.width
    )
    displayImage
  }

}
