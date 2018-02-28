package ru.nsu.fit.g15201.boltava.model.graphics

import javafx.scene.paint.Color

import ru.nsu.fit.g15201.boltava.model.canvas.IDrawable
import ru.nsu.fit.g15201.boltava.model.canvas.geometry.Point
import ru.nsu.fit.g15201.boltava.model.logic.Cell

trait IDrawer {
  def drawLine(drawable: IDrawable, from: Point, to: Point, color: Color)
  def drawCell(drawable: IDrawable, cell: Cell, color: Color)
  def drawGrid(drawable: IDrawable, grid: Array[Array[Cell]], color: Color)
}
