package ru.nsu.fit.g15201.boltava.domain_layer.logic

import ru.nsu.fit.g15201.boltava.presentation_layer.main.ICellStateObserver

trait ICellStateProvider {
  def addCellStateObserver(cellStateObserver: ICellStateObserver)
  def removeCellStateObserver(cellStateObserver: ICellStateObserver)
}
