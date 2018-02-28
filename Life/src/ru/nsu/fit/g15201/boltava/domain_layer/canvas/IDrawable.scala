package ru.nsu.fit.g15201.boltava.domain_layer.canvas

import javafx.scene.paint.Color

import ru.nsu.fit.g15201.boltava.domain_layer.canvas.geometry.Point

trait IDrawable {
  def getHeight: Double
  def getWidth: Double
  def setColor(point: Point, color: Color): Unit
  def getColor(point: Point): Color
  def draw(points: Array[Point], color: Color = Color.BLACK): Unit
}
