package ru.nsu.fit.g15201.boltava.domain_layer.storage

import ru.nsu.fit.g15201.boltava.domain_layer.filter.RawImage

import scala.collection.mutable

object ImageHolder extends IImageHolder with IImageProvider {

  val observers = new mutable.HashSet[IImageObserver]()

  var mainImage: Option[RawImage] = None
  var croppedImage: Option[RawImage] = None
  var filteredImage: Option[RawImage] = None

  override def getMainImage: Option[RawImage] = mainImage

  override def setMainImage(transformableImage: RawImage): Unit = {
    mainImage = Some(transformableImage)
    observers.foreach({ o =>
      o.onImageReset()
      o.onMainImageChanged(transformableImage)
    })

  }

  override def getCroppedImage: Option[RawImage] = croppedImage

  override def setCroppedImage(transformableImage: RawImage): Unit = {
    croppedImage = Some(transformableImage)
    observers.foreach(_.onCroppedImageChanged(transformableImage))
  }

  override def getFilteredImage: Option[RawImage] = filteredImage

  override def setFilteredImage(transformableImage: RawImage): Unit = {
    filteredImage = Some(transformableImage)
    observers.foreach(_.onFilteredImageChanged(transformableImage))
  }

  override def subscribe(imageObserver: IImageObserver): Unit = {
    observers.add(imageObserver)
  }

  override def unsubscribe(imageObserver: IImageObserver): Unit = {
    observers.remove(imageObserver)
  }

}
