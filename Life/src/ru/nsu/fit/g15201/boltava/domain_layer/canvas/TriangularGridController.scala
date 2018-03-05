package ru.nsu.fit.g15201.boltava.domain_layer.canvas

import ru.nsu.fit.g15201.boltava.domain_layer.canvas.geometry.{DoublePoint, Point}
import ru.nsu.fit.g15201.boltava.domain_layer.logic.{Cell, HexagonCell}

class TriangularGridController(private val triangleSideSize: Int) extends IGridController {

  private val sideSize: Double = triangleSideSize
  private val halfSize: Double = sideSize / 2
  private val triangleHeight: Double = Math.sqrt(3)*sideSize/2
  private val xStep: Double = sideSize/2
  private val yStep: Double = triangleHeight
  private val bias: (Double, Double) = (halfSize, triangleHeight/2)

  def getSideSize: Int = triangleSideSize

  override def generateGrid(width: Int, height: Int): Array[Array[Cell]] = {
    val grid = new Array[Array[HexagonCell]](height)

    for (x <- 0 until height) {
      grid(x) = new Array[HexagonCell](width)
      for (y <- 0 until width) {
        val center = getCellCenter(x,y)
//        val vertices = getVerticesForTriangleCenter(center)
        val vertices = getVerticesForTriangleCoords((x, y))
        grid(x)(y) = new HexagonCell((center.x.toInt, center.y.toInt), vertices, y, x)
      }
    }

    grid.asInstanceOf[Array[Array[Cell]]]
  }

  override def getCellCenter(point: Point): DoublePoint = {
    val x =  bias._1 + point.y.toDouble * xStep
    val y =  bias._2 + point.x.toDouble * yStep
    (x, y)
  }

  private def getVerticesForTriangleCenter(center: DoublePoint): Array[Point] = {
    val sign = if (trianglePointsTop(getCellByPoint(center))) 1 else -1

    Array[Point](
      ((center.x + halfSize).round.toInt, (center.y - sign*triangleHeight/2).round.toInt),
      ((center.x - halfSize).round.toInt, (center.y - sign*triangleHeight/2).round.toInt),
      (center.x.round.toInt, (center.y + sign*triangleHeight/2).round.toInt)
    )
  }

  private def getVerticesForTriangleCoords(point: Point): Array[Point] = {
    val sign = if (trianglePointsTop(point)) 1 else -1
    val center = getCellCenter(point)

    Array[Point](
      ((center.x + halfSize).round.toInt, (center.y + sign*triangleHeight/2).round.toInt),
      ((center.x - halfSize).round.toInt, (center.y + sign*triangleHeight/2).round.toInt),
      (center.x.round.toInt, (center.y - sign*triangleHeight/2).round.toInt)
    )
  }

  /**
    * Get cell coordinate by Cartesian coordinate point
    *
    * @param point a point in Cartesian coordinate system
    * @return cell coordinate in custom (internal) coordinate system
    */
  override def getCellByPoint(point: DoublePoint): Point = {
    val boxHeight = triangleHeight
    val boxWidth = sideSize

    val boxX = (point.x / boxWidth).floor.toInt
    val boxY = (point.y / boxHeight).floor.toInt

    val innerX = point.x - boxX * boxWidth
    val innerY = point.y - boxY * boxHeight

    val evenRow = boxY % 2 == 0

    if (evenRow) {
      val slope = Math.sqrt(3)
      if (innerY < slope * Math.abs(innerX - halfSize)) {
        if (innerX < halfSize) {
          (boxY, 2 * boxX - 1)
        } else {
          (boxY, 2 * boxX + 1)
        }
      } else {
        (boxY, 2 * boxX)
      }
    } else {
      val slope = -Math.sqrt(3)
      val bias = triangleHeight
      if (innerY < slope * Math.abs(innerX - halfSize) + bias) {
        (boxY, 2 * boxX)
      } else {
        if (innerX < halfSize) {
          (boxY, 2 * boxX - 1)
        } else {
          (boxY, 2 * boxX + 1)
        }
      }
    }
  }

  override def getCellNeighbors(point: Point): Array[Point] = {
    val x = point.x
    val y = point.y

    val verticalNeighbor = if (trianglePointsTop(point)) (x+1, y) else (x-1, y)

    Array[Point](
      verticalNeighbor,
      (x, y+1),
      (x, y-1)
    )
  }

  override def getCellDistantNeighbors(point: Point): Array[Point] = {
    val x = point.x
    val y = point.y

    val sign = if (trianglePointsTop(point)) 1 else -1

    Array[Point](
      (x-sign*1, y),
      (x+sign*1, y+1),
      (x+sign*1, y-1)
    )
  }

  override def getCartesianFieldSize(columnsCount: Int, rowsCount: Int): (Double, Double) = {
    val fieldWidth = (columnsCount * xStep + 2 * bias._1).ceil
    val fieldHeight = (rowsCount * triangleHeight).ceil
    (fieldWidth, fieldHeight)
  }

  private def trianglePointsTop(point: Point): Boolean = {
    (point.y + (point.x%2)) % 2 == 0
  }

}
