package ru.nsu.fit.g15201.boltava.domain_layer.controllers

trait ILayerVisibilityProvider {

  def subscribe(visibilityObserver: ILayerVisibilityObserver)
  def unsubscribe(visibilityObserver: ILayerVisibilityObserver)

}
