package ru.nsu.fit.g15201.boltava.presentation_layer.main

import ru.nsu.fit.g15201.boltava.domain_layer.logic.Cell

trait ICellStateObserver {
  def onCellStateChange(cell: Cell)
  def onCellsStateChange(cell: Array[Array[Cell]])
}
