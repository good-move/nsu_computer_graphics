package ru.nsu.fit.g15201.boltava.model.graphics

import ru.nsu.fit.g15201.boltava.model.logic.Hexagon

import scala.collection.mutable.ArrayBuffer

class HexagonalGridController(private val hexSideSize: Int) extends IGridController[Hexagon] {

  private val _size: Double = hexSideSize
  private val R: Double = _size
  private val r: Double = Math.sqrt(3)* _size/2.0
  private val xStep: Double = 2.0*r
  private val yStep: Double = 1.5*R
  private val bias: (Double, Double) = (r, R)

  private val ROTATION_ANGLE = 60.0
  private val OFFSET_ANGLE = 30.0

  def getSideSize: Int = hexSideSize

  override def generateGrid(width: Int, height: Int): Array[Array[Hexagon]] = {
    val grid = new Array[Array[Hexagon]](height)

    for (x <- 0 until height) {
      grid(x) = new Array[Hexagon](width)
      for (y <- 0 until width) {
        val center = getCellCenter(x,y)
        val vertices = getVerticesForHexCenter(center)
        grid(x)(y) = Hexagon((center.x.toInt, center.y.toInt), vertices)
      }
    }

    grid
  }

  override def getCellCenter(point: Point): DoublePoint = {
    val x =  bias._1 + point.y.toDouble * xStep + r * (point.x & 1).toDouble
    val y =  bias._2 + point.x.toDouble * yStep
    (x, y)
  }

  private def getVerticesForHex(point: Point): Array[Point] = {
    getVerticesForHexCenter(getCellCenter(point))
  }

  private def getVerticesForHexCenter(center: DoublePoint): Array[Point] = {
    var vertices = ArrayBuffer[Point]()

    var angle_deg: Double = OFFSET_ANGLE
    for (_ <- 0 to 6) {
      angle_deg += ROTATION_ANGLE
      val angle_rad = angle_deg/180.0  * Math.PI
      vertices.append((
        Math.round(center.x + _size * Math.cos(angle_rad)).toInt,
        Math.round(center.y + _size * Math.sin(angle_rad)).toInt
      ))
    }

    vertices.toArray
  }

  def createHex(point: Point): Hexagon = {
    val center = getCellCenter(point)
    val vertices = getVerticesForHex(point)
    Hexagon(new Point(center.x, center.y), vertices)
  }

}
