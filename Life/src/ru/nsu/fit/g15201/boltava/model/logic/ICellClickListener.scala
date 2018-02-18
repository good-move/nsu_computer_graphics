package ru.nsu.fit.g15201.boltava.model.logic

import ru.nsu.fit.g15201.boltava.model.canvas.geometry.Polygon

trait ICellClickListener {
  def onCellClicked(cell: Cell with Polygon)
}
