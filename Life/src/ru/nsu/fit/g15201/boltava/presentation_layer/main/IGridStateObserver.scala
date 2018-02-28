package ru.nsu.fit.g15201.boltava.view.main

import ru.nsu.fit.g15201.boltava.model.logic.Cell

trait IGridStateObserver {
  def onGridStructureChange(grid: Array[Array[Cell]], aliveCells: Array[Cell])
}
