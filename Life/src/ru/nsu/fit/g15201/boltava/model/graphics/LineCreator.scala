package ru.nsu.fit.g15201.boltava.model.graphics

import ru.nsu.fit.g15201.boltava.model.canvas.geometry.Point

/**
  * Base interface for all line drawers
  *
  * It's up to the concrete implementation to choose the
  * line drawing algorithm
  */
trait LineCreator {
  def getLinePoints(from: Point, to: Point): Array[Point]
}
