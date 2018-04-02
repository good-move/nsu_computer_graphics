package ru.nsu.fit.g15201.boltava.domain_layer.mesh

import ru.nsu.fit.g15201.boltava.domain_layer.logic.function.IFunction2D
import ru.nsu.fit.g15201.boltava.domain_layer.logic.settings.Settings
import ru.nsu.fit.g15201.boltava.domain_layer.primitives.{Dimensions, Point3D}

object MeshGenerator {

  def generate(fieldDimensions: Dimensions, settings: Settings, function: IFunction2D): CellGrid = {
    val cellWidth = fieldDimensions.width / (settings.xNodes-1)
    val cellHeight = fieldDimensions.height / (settings.yNodes-1)
    new CellGrid(settings.xNodes, settings.yNodes, Dimensions(cellWidth, cellHeight), function)
  }

  class CellGrid(nodesX: Int, nodesY: Int, cellDimensions: Dimensions, function: IFunction2D) {
    private val cellWidth: Double = cellDimensions.width
    private val cellHeight: Double = cellDimensions.height

    val cellsX: Int = nodesX-1
    val cellsY: Int = nodesY-1

    val width: Double = cellWidth * cellsX
    val height: Double = cellHeight * cellsY

    val controlNodes: Seq[ControlNode] = {
      val domainXOffset = function.domain.get.xRange.lower.value
      val domainYOffset = function.domain.get.yRange.lower.value

      val functionDx = function.domain.get.xRange.size.value / width
      val functionDy = function.domain.get.yRange.size.value / height

      for {
        y <- 0.0 until height + cellHeight by cellHeight
        x <- 0.0 until width + cellWidth by cellWidth
      } yield {
        val functionX = x*functionDx + domainXOffset
        val functionY = y*functionDy + domainYOffset
        ControlNode(Point3D(x, y, function(functionX, functionY)))
      }
    }

    println(controlNodes.length)

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
