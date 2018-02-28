package ru.nsu.fit.g15201.boltava.presentation_layer.main

import ru.nsu.fit.g15201.boltava.domain_layer.logic.Cell

trait IGridStateObserver {
  def onGridStructureChange(grid: Array[Array[Cell]], aliveCells: Array[Cell])
}
