package ru.nsu.fit.g15201.boltava.model.graphics
import javafx.scene.paint.Color

import ru.nsu.fit.g15201.boltava.model.canvas.IDrawable
import ru.nsu.fit.g15201.boltava.model.canvas.geometry.Point
import ru.nsu.fit.g15201.boltava.model.logic.Cell

class BresenhamDrawer extends IDrawer {

  override def drawLine(drawable: IDrawable, from: Point, to: Point, color: Color): Unit = {
    val f = if (from.x > to.x) from else to
    val t = if (from.x > to.x) to else from
    val linePoints = BresenhamLineCreator.getLinePoints(f, t)
    drawable.draw(linePoints)
  }

  override def drawCell(drawable: IDrawable, cell: Cell, color: Color): Unit = {
    val vertices = cell.getVertices
    val verticesCount = vertices.length
    for (i <- vertices.indices) {
      drawLine(drawable, vertices(i), vertices((i + 1) % verticesCount), color)
    }
  }

  override def drawGrid(drawable: IDrawable, grid: Array[Array[Cell]], color: Color): Unit = {
    grid.foreach(_.foreach(cell => drawCell(drawable, cell, color)))
  }

}
