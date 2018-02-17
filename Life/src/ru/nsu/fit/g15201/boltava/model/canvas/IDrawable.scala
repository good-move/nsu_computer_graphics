package ru.nsu.fit.g15201.boltava.model.canvas

import javafx.scene.paint.Color

trait IDrawable {
  def getHeight: Double
  def getWidth: Double
  def setColor(point: Point, color: Color): Unit
  def getColor(point: Point): Color
  def draw(points: Array[Point], color: Color = Color.BLACK): Unit
}
