package ru.nsu.fit.g15201.boltava.model.canvas

import ru.nsu.fit.g15201.boltava.model.canvas.geometry.{DoublePoint, Point}
import ru.nsu.fit.g15201.boltava.model.logic.{Cell, HexagonCell}

import scala.collection.mutable.ArrayBuffer

class HexagonalGridController(private val hexSideSize: Int) extends IGridController {

  private val _size: Double = hexSideSize
  private val R: Double = _size
  private val r: Double = Math.sqrt(3)* _size/2.0
  private val xStep: Double = 2.0*r
  private val yStep: Double = 1.5*R
  private val bias: (Double, Double) = (r, R)

  private val ROTATION_ANGLE = 60.0
  private val OFFSET_ANGLE = 30.0

  def getSideSize: Int = hexSideSize

  override def generateGrid(width: Int, height: Int): Array[Array[Cell]] = {
    val grid = new Array[Array[HexagonCell]](height)

    for (x <- 0 until height) {
      grid(x) = new Array[HexagonCell](width)
      for (y <- 0 until width) {
        val center = getCellCenter(x,y)
        val vertices = getVerticesForHexCenter(center)
        grid(x)(y) = new HexagonCell((center.x.toInt, center.y.toInt), vertices)
      }
    }

    grid.asInstanceOf[Array[Array[Cell]]]
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
    val vertices = ArrayBuffer[Point]()

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

  def createHex(point: Point): HexagonCell = {
    val center = getCellCenter(point)
    val vertices = getVerticesForHex(point)
    new HexagonCell(new Point(center.x, center.y), vertices)
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

  override def getCellNeighbors(point: Point): Array[Point] = {
    val x = point.x
    val y = point.y
    val sign = if (x % 2 == 0) -1 else 1

    Array[Point](
      (x, y+1),
      (x, y-1),
      (x-1, y),
      (x-1, y+sign),
      (x+1, y),
      (x+1, y+sign)
    )
  }

  override def getCellDistantNeighbors(point: Point): Array[Point] = {
    val x = point.x
    val y = point.y
    val isEven = if (point.x % 2 == 0) 1 else 0
    val isOdd = if (point.x % 2 != 0) 1 else 0

    Array[Point](
      (x-2, y),
      (x+2, y),
      (x-1, y+1+isOdd),
      (x-1, y-1-isEven),
      (x+1, y+1+isOdd),
      (x+1, y-1-isEven)
    )
  }

  override def getCartesianFieldSize(width: Int, height: Int): (Double, Double) = {
    val w = 2*r * (width+1)
    val h = R*1.5*height + R
    (w + bias._1, h)
  }

}
