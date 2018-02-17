package ru.nsu.fit.g15201.boltava.model.logic

import ru.nsu.fit.g15201.boltava.model.canvas.{Polygon, Point}

class Hexagon(private val _center: Point, private val _vertices: Array[Point]) extends Polygon {

  override def getCenter: Point = _center

  override def getVertices: Array[Point] = _vertices
}


object Hexagon {
  def apply(center: Point, vertices: Array[Point]): Hexagon = new Hexagon(center, vertices)
}