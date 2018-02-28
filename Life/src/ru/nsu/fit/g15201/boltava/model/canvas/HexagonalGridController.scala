package ru.nsu.fit.g15201.boltava.model.canvas

import ru.nsu.fit.g15201.boltava.model.canvas.geometry.{DoublePoint, Point}
import ru.nsu.fit.g15201.boltava.model.logic.{Cell, HexagonCell}

import scala.collection.mutable.ArrayBuffer

class HexagonalGridController(private val hexSideSize: Int) extends IGridController {

  private val size: Double = hexSideSize
  private val R: Double = size // radius of the hexagon circumcircle
  private val r: Double = Math.sqrt(3)* size/2.0 // radius of the hexagon incircle
  private val xStep: Double = 2.0*r
  private val yStep: Double = 1.5*R
  private val bias: (Double, Double) = (r, R)

  private val ROTATION_ANGLE = 60.0
  private val OFFSET_ANGLE = 30.0

  override def generateGrid(width: Int, height: Int): Array[Array[Cell]] = {
    val grid = new Array[Array[HexagonCell]](height)

    for (x <- 0 until height) {
      grid(x) = new Array[HexagonCell](width)
      for (y <- 0 until width) {
        val center = getCellCenter(x,y)
        val vertices = getVerticesForHexCenter(center)
        grid(x)(y) = new HexagonCell((center.x.toInt, center.y.toInt), vertices, x, y)
      }
    }

    grid.asInstanceOf[Array[Array[Cell]]]
  }

  override def getCellCenter(point: Point): DoublePoint = {
    val x =  bias._1 + point.y.toDouble * xStep + r * (point.x & 1).toDouble
    val y =  bias._2 + point.x.toDouble * yStep
    (x, y)
  }

  private def getVerticesForHexCenter(center: DoublePoint): Array[Point] = {
    val vertices = ArrayBuffer[Point]()

    var angle_deg: Double = OFFSET_ANGLE
    for (_ <- 0 to 6) {
      angle_deg += ROTATION_ANGLE
      val angle_rad = angle_deg/180.0  * Math.PI
      vertices.append((
        (center.x + size * Math.cos(angle_rad)).ceil.toInt,
        (center.y + size * Math.sin(angle_rad)).ceil.toInt
      ))
    }

    vertices.toArray
  }

  /**
    * Get cell coordinate by Cartesian coordinate point
    *
    * @param point a point in Cartesian coordinate system
    * @return cell coordinate in custom (internal) coordinate system
    */
  override def getCellByPoint(point: DoublePoint): Point = {
    val boxHeight = 1.5*R
    val boxWidth = 2.0*r
    // figure out hexagon box coordinates
    val boxY: Int = Math.floor(point.y / boxHeight).toInt
    val xBiased: Double = point.x - r*(boxY%2)
    val boxX: Int = Math.floor(xBiased / boxWidth).toInt

    // figure out point coordinates inside the box
    val innerY: Double = point.y - boxHeight * boxY.toDouble
    val innerX: Double = xBiased - boxWidth * boxX.toDouble

    // figure out, what hexagon part we're in
    val slope = R/r/2
    val lineValue = slope*Math.abs(innerX - boxWidth/2)
    if (innerY < lineValue) {
      if (innerX < boxWidth/2) {
        (boxY - 1, boxX - (boxY+1)%2)
      } else {
        (boxY - 1, boxX + boxY%2)
      }
    } else if (innerY > lineValue) {
      (boxY, boxX)
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

  override def getCartesianFieldSize(columnsCount: Int, rowsCount: Int): (Double, Double) = {
    val fieldWidth = 2 * r * (columnsCount+1)
    val fieldHeight = R * 1.5 * rowsCount + R

    (fieldWidth + bias._1, fieldHeight)
  }

}
