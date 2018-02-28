package ru.nsu.fit.g15201.boltava.model.canvas

import ru.nsu.fit.g15201.boltava.model.canvas.geometry.{DoublePoint, Point}
import ru.nsu.fit.g15201.boltava.model.logic.{Cell, HexagonCell}

class SquareGridController(private val squareSideSize: Int) extends IGridController {

  private val sideSize: Double = squareSideSize
  private val halfSize: Double = sideSize / 2
  private val xStep: Double = sideSize
  private val yStep: Double = sideSize
  private val bias: (Double, Double) = (halfSize, halfSize)

  def getSideSize: Int = squareSideSize

  override def generateGrid(width: Int, height: Int): Array[Array[Cell]] = {
    val grid = new Array[Array[HexagonCell]](height)

    for (x <- 0 until height) {
      grid(x) = new Array[HexagonCell](width)
      for (y <- 0 until width) {
        val center = getCellCenter(x,y)
        val vertices = getVerticesForSquareCenter(center)
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

  private def getVerticesForSquareCenter(center: DoublePoint): Array[Point] = {
    Array[Point](
      ((center.x + halfSize).round.toInt, (center.y + halfSize).round.toInt),
      ((center.x - halfSize).round.toInt, (center.y + halfSize).round.toInt),
      ((center.x - halfSize).round.toInt, (center.y - halfSize).round.toInt),
      ((center.x + halfSize).round.toInt, (center.y - halfSize).round.toInt)
    )
  }

  /**
    * Get cell coordinate by Cartesian coordinate point
    *
    * @param point a point in Cartesian coordinate system
    * @return cell coordinate in custom (internal) coordinate system
    */
  override def getCellByPoint(point: DoublePoint): Point = {
    val x = (point.y / sideSize).toInt
    val y = (point.x / sideSize).toInt
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

  override def getCartesianFieldSize(columnsCount: Int, rowsCount: Int): (Double, Double) = {
    val fieldWidth = (columnsCount+1) * sideSize
    val fieldHeight = (rowsCount+1) * sideSize
    (fieldWidth, fieldHeight)
  }

}
