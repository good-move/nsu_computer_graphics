package ru.nsu.fit.g15201.boltava.model.canvas

trait Polygon {
  def getCenter: Point
  def getVertices: Array[Point]
}
