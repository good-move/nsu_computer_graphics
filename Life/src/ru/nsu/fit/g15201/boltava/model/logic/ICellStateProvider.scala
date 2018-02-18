package ru.nsu.fit.g15201.boltava.model.logic

import ru.nsu.fit.g15201.boltava.view.ICellStateObserver

trait ICellStateProvider {
  def subscribe(cellStateObserver: ICellStateObserver)
  def unsubscribe(cellStateObserver: ICellStateObserver)
}
