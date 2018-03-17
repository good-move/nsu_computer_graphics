package ru.nsu.fit.g15201.boltava.domain_layer.storage

import ru.nsu.fit.g15201.boltava.domain_layer.filter.RawImage

trait IImageHolder {

  def getMainImage: Option[RawImage]
  def setMainImage(transformableImage: RawImage)

  def getCroppedImage: Option[RawImage]
  def setCroppedImage(transformableImage: RawImage)

  def getFilteredImage: Option[RawImage]
  def setFilteredImage(transformableImage: RawImage)


}
