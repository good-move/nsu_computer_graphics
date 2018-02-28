package ru.nsu.fit.g15201.boltava.presentation_layer.main

trait IGridStateProvider {
  def addGridStateObserver(gridStateObserver: IGridStateObserver)
  def removeGridStateObserver(gridStateObserver: IGridStateObserver)
}
