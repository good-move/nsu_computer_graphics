package ru.nsu.fit.g15201.boltava.domain_layer.graphics

import ru.nsu.fit.g15201.boltava.domain_layer.canvas.geometry.Point

import scala.collection.mutable.ArrayBuffer

object BresenhamLineCreator extends LineCreator {

  override def getLinePoints(from: Point, to: Point): Array[Point] = {
    val dx: Int = Math.abs(from.x - to.x)
    val dy: Int = Math.abs(from.y - to.y)
    val linePoints = ArrayBuffer[Point]()

    val yStep = if (from.y <= to.y) 1 else -1
    val xStep = if (from.x <= to.x) 1 else -1
    var error: Int = 0
    var currentX = from.x
    var currentY = from.y

    val pointsToAdd: Int = Math.max(dx, dy)+1

    for (_ <- 1 to pointsToAdd) {
      linePoints.append((currentX, currentY))
      if (dx >= dy) {
        error += dy
        if (2 * error >= dx) {
          currentY += yStep
          error -= dx
        }
        currentX += xStep
      } else {
        error += dx
        if (2 * error >= dy) {
          currentX += xStep
          error -= dy
        }
        currentY += yStep
      }
    }

    linePoints.toArray
  }
}
