package ru.nsu.fit.g15201.boltava.model.logic

import ru.nsu.fit.g15201.boltava.model.canvas.Point

class Hexagon(private val _center: Point, private val _vertices: Array[Point]) extends Cell {

  def center: Point = _center

  def vertices: Array[Point] = _vertices

}


object Hexagon {
  def apply(center: Point, vertices: Array[Point]): Hexagon = new Hexagon(center, vertices)
}