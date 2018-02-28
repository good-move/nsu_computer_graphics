package ru.nsu.fit.g15201.boltava.view.main

trait IGridStateProvider {
  def addGridStateObserver(gridStateObserver: IGridStateObserver)
  def removeGridStateObserver(gridStateObserver: IGridStateObserver)
}
