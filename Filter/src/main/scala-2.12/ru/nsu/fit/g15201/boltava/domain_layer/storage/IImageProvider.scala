package ru.nsu.fit.g15201.boltava.domain_layer.storage

trait IImageProvider {

  def subscribe(imageObserver: IImageObserver)
  def unsubscribe(imageObserver: IImageObserver)

}
