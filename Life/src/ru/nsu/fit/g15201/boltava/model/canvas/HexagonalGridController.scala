package ru.nsu.fit.g15201.boltava.model.canvas

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

  /**
    * Get cell coordinate by Cartesian coordinate point
    *
    * @param point a point in Cartesian coordinate system
    * @return cell coordinate in custom (internal) coordinate system
    */
  override def getCellByPoint(point: DoublePoint): Point = {
    val H = 1.5*R
    val W = 2.0*r
    // figure out hexagon box coordinates
    val yt: Int = Math.floor(point.y / H).toInt
    val xBiased: Double = point.x - r*(yt%2)
    val xt: Int = Math.floor(xBiased / W).toInt

    // figure out point coordinates inside the box
    val yIn: Double = point.y- H*yt.toDouble
    val xIn: Double = xBiased - W*xt.toDouble

    // figure out, what hexagon part we're in
    var error = false
    val slope = R/r/2
    val lineValue = slope*Math.abs(xIn - W/2)
    if (yIn < lineValue) {
      if (xIn < W/2) {
        (yt - 1, xt - (yt+1)%2)
      } else {
        (yt - 1, xt + yt%2)
      }
    } else if (yIn > lineValue) {
      (yt, xt)
    } else {
      (-1, -1)
    }
  }

}
