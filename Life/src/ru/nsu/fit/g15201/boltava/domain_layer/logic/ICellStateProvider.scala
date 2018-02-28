package ru.nsu.fit.g15201.boltava.model.logic

import ru.nsu.fit.g15201.boltava.view.main.ICellStateObserver

trait ICellStateProvider {
  def addCellStateObserver(cellStateObserver: ICellStateObserver)
  def removeCellStateObserver(cellStateObserver: ICellStateObserver)
}
