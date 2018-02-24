package ru.nsu.fit.g15201.boltava.model.canvas

import ru.nsu.fit.g15201.boltava.model.canvas.geometry.{DoublePoint, Point}
import ru.nsu.fit.g15201.boltava.model.logic.{Cell, HexagonCell}

import scala.collection.mutable.ArrayBuffer

class SquareGridController(private val squareSideSize: Int) extends IGridController {

  private val _size: Double = squareSideSize
  private val _halfSize: Double = _size / 2
  private val xStep: Double = _size
  private val yStep: Double = _size
  private val bias: (Double, Double) = (_size/2, _size/2)

  private val ROTATION_ANGLE = 90.0
  private val OFFSET_ANGLE = 45.0

  def getSideSize: Int = squareSideSize

  override def generateGrid(width: Int, height: Int): Array[Array[Cell]] = {
    val grid = new Array[Array[HexagonCell]](height)

    for (x <- 0 until height) {
      grid(x) = new Array[HexagonCell](width)
      for (y <- 0 until width) {
        val center = getCellCenter(x,y)
        val vertices = getVerticesForHexCenter(center)
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

  private def getVerticesForHex(point: Point): Array[Point] = {
    getVerticesForHexCenter(getCellCenter(point))
  }

  private def getVerticesForHexCenter(center: DoublePoint): Array[Point] = {
    Array[Point](
      ((center.x + _halfSize).round.toInt, (center.y + _halfSize).round.toInt),
      ((center.x - _halfSize).round.toInt, (center.y + _halfSize).round.toInt),
      ((center.x - _halfSize).round.toInt, (center.y - _halfSize).round.toInt),
      ((center.x + _halfSize).round.toInt, (center.y - _halfSize).round.toInt)
    )
  }

  /**
    * Get cell coordinate by Cartesian coordinate point
    *
    * @param point a point in Cartesian coordinate system
    * @return cell coordinate in custom (internal) coordinate system
    */
  override def getCellByPoint(point: DoublePoint): Point = {
    val x = (point.y / _size).toInt
    val y = (point.x / _size).toInt
    (x, y)
  }

  override def getCellNeighbors(point: Point): Array[Point] = {
    val x = point.x
    val y = point.y

    Array[Point](
      (x-1, y),
      (x+1, y),
      (x, y+1),
      (x, y-1)
    )
  }

  override def getCellDistantNeighbors(point: Point): Array[Point] = {
    val x = point.x
    val y = point.y
    Array[Point](
      (x-1, y-1),
      (x+1, y-1),
      (x+1, y+1),
      (x-1, y+1)
    )
  }

  override def getCartesianFieldSize(width: Int, height: Int): (Double, Double) = {
    val w = (width+1) * _size
    val h = (height+1) * _size
    (w, h)
  }

}
