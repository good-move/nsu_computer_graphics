package ru.nsu.fit.g15201.boltava.domain_layer.controllers


trait GridVisibilityObserver {
  def onGridVisibilityChanged(visible: Boolean)
}

trait IsolinesVisibilityObserver {
  def onIsolinesVisibilityChanged(visible: Boolean)
}

trait IntersectionPointsVisibilityObserver {
  def onIntersectionPointsVisibilityChanged(visible: Boolean)
}


trait ILayerVisibilityObserver
  extends GridVisibilityObserver
  with IsolinesVisibilityObserver
  with IntersectionPointsVisibilityObserver

