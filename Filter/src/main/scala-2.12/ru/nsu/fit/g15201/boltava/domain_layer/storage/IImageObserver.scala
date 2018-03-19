package ru.nsu.fit.g15201.boltava.domain_layer.storage

import ru.nsu.fit.g15201.boltava.domain_layer.filter.RawImage

trait IImageObserver {

  def onMainImageChanged(newImage: RawImage)
  def onCroppedImageChanged(newImage: RawImage)
  def onFilteredImageChanged(newImage: RawImage)

  def onImageReset()

}
