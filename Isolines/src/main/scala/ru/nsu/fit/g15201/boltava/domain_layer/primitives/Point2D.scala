package ru.nsu.fit.g15201.boltava.domain_layer.primitives

case class Point2D(x: Double, y: Double) {

  def +(other: Point2D): Point2D = {
    Point2D(this.x + other.x, this.y + other.y)
  }

  def -(other: Point2D): Point2D = {
    Point2D(this.x - other.x, this.y - other.y)
  }

  def *(scalar: Double): Point2D = {
    Point2D(x * scalar, y * scalar)
  }

  def /(scalar: Double): Point2D = {
    Point2D(x / scalar, y / scalar)
  }

}

