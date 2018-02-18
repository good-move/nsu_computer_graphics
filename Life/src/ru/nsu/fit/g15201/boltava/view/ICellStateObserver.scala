package ru.nsu.fit.g15201.boltava.view

import ru.nsu.fit.g15201.boltava.model.canvas.geometry.Polygon
import ru.nsu.fit.g15201.boltava.model.logic.Cell

trait ICellStateObserver {
  def onCellStateChange(cell: Cell with Polygon)
}
