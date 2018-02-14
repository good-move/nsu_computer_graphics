package ru.nsu.fit.g15201.boltava.model.graphics

import ru.nsu.fit.g15201.boltava.model.logic.Hexagon

import scala.collection.mutable.ArrayBuffer

class HexagonalGridController(private val hexSideSize: Int) extends IGridController[Hexagon] {

  private val _size: Double = hexSideSize.toDouble
  private val R: Double = _size
  private val r: Double = Math.sqrt(3.0)/2.0 * _size
  private val xStep: Double = Math.sqrt(3.0)*_size
  private val yStep: Double = 3.0*_size/2.0
  private val bias = (r, R)

  private val ROTATION_ANGLE = 60
  private val OFFSET_ANGLE = 30

  def getSideSize: Int = hexSideSize

  override def generateGrid(width: Int, height: Int): Array[Array[Hexagon]] = {
    val grid = new Array[Array[Hexagon]](height)

    for (x <- 0 until height) {
      grid(x) = new Array[Hexagon](width)
      for (y <- 0 until width) {
        val center = getCenterPixelCoords(x,y)
        val vertices = getVerticesForHexCenter(center)
        grid(x)(y) = Hexagon(center, vertices)
      }
    }

    grid
  }

  override def getCenterPixelCoords(point: Point): Point = {
    (
      Math.round(bias._1 + point.y.toDouble * xStep + r * (point.x & 1)).toInt,
      Math.round(bias._2 + point.x.toDouble * yStep).toInt
    )
  }

  def getVerticesForHex(point: Point): Array[Point] = {
    getVerticesForHexCenter(getCenterPixelCoords(point))
  }

  def getVerticesForHexCenter(center: Point): Array[Point] = {
    var vertices = ArrayBuffer[Point]()

    var angle_deg = OFFSET_ANGLE
    for (i <- 0 to 6) {
      angle_deg += ROTATION_ANGLE
      val angle_rad = Math.PI / 180 * angle_deg
      vertices.append((
        Math.round(center.x + _size * Math.cos(angle_rad)).toInt,
        Math.round(center.y + _size * Math.sin(angle_rad)).toInt
      ))
    }

    vertices.toArray
  }

}
