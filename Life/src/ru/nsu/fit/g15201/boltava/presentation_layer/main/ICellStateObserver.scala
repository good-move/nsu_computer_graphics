package ru.nsu.fit.g15201.boltava.view.main

import ru.nsu.fit.g15201.boltava.model.logic.Cell

trait ICellStateObserver {
  def onCellStateChange(cell: Cell)
  def onCellsStateChange(cell: Array[Array[Cell]])
}
