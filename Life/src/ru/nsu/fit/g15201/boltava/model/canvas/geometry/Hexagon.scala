package ru.nsu.fit.g15201.boltava.model.canvas.geometry

class Hexagon(private val center: Point, private val vertices: Array[Point]) extends Polygon {

  override def getCenter: Point = center

  override def getVertices: Array[Point] = vertices
}

object Hexagon {
  def apply(center: Point, vertices: Array[Point]): Hexagon = new Hexagon(center, vertices)
}