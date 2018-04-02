package ru.nsu.fit.g15201.boltava.domain_layer.mesh

import ru.nsu.fit.g15201.boltava.domain_layer.logic.function.IFunction2D
import ru.nsu.fit.g15201.boltava.domain_layer.logic.settings.Settings
import ru.nsu.fit.g15201.boltava.domain_layer.primitives.{Dimensions, Point3D}

object MeshGenerator {

  def generate(fieldDimensions: Dimensions, settings: Settings, function: IFunction2D): CellGrid = {
    CoordinatesMapper.setMapping(fieldDimensions, function.domain.get)

    val cellWidth = fieldDimensions.width / (settings.xNodes-1)
    val cellHeight = fieldDimensions.height / (settings.yNodes-1)

    new CellGrid(settings.xNodes, settings.yNodes, Dimensions(cellWidth, cellHeight), function)
  }

  class CellGrid(nodesX: Int, nodesY: Int, cellDimensions: Dimensions, function: IFunction2D) {
    val cellWidth: Double = cellDimensions.width
    val cellHeight: Double = cellDimensions.height

    val cellsX: Int = nodesX-1
    val cellsY: Int = nodesY-1

    val width: Double = cellWidth * cellsX
    val height: Double = cellHeight * cellsY

    val controlNodes: Seq[ControlNode] = {
      for {
        y <- 0.0 until height + cellHeight by cellHeight
        x <- 0.0 until width + cellWidth by cellWidth
      } yield {
        val domainPoint = CoordinatesMapper.toDomain(x, y)
        val functionX = domainPoint.x
        val functionY = domainPoint.y
        ControlNode(Point3D(x, y, function(functionX, functionY)))
      }
    }

    val grid: Seq[Cell] = for {
      y <- 0 until cellsY
      x <- 0 until cellsX
    } yield {
      val topLeftOffset = y * nodesX + x
      Cell(
        topLeft = controlNodes(topLeftOffset),
        topRight = controlNodes(topLeftOffset + 1),
        bottomRight = controlNodes(topLeftOffset + nodesX + 1),
        bottomLeft = controlNodes(topLeftOffset + nodesX)
      )
    }

    def apply(xIndex: Int, yIndex: Int): Cell = grid(yIndex * cellsX + xIndex)

  }

}
