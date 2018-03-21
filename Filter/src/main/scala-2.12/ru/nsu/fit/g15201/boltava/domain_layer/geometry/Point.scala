package ru.nsu.fit.g15201.boltava.domain_layer.geometry

abstract class Point[T](_x: T, _y: T) {
  def getX: T = _x
  def getY: T = _y
}

case class IntPoint(x: Int, y: Int) extends Point[Int](x, y) {
  def +(other: IntPoint): IntPoint = {
    IntPoint(x + other.x, y + other.y)
  }

  def -(other: IntPoint): IntPoint = {
    IntPoint(x - other.x, y - other.y)
  }

  def *(factor: Int): IntPoint = {
    IntPoint(x*factor, y*factor)
  }

  def /(factor: Int): IntPoint = {
    IntPoint(x/factor, y/factor)
  }
}

case class DoublePoint(x: Double, y: Double) extends Point[Double](x, y) {

  def +(other: DoublePoint): DoublePoint = {
    DoublePoint(x + other.x, y + other.y)
  }

  def -(other: DoublePoint): DoublePoint = {
    DoublePoint(x - other.x, y - other.y)
  }

  def *(factor: Double): DoublePoint = {
    DoublePoint(x*factor, y*factor)
  }

  def /(factor: Double): DoublePoint = {
    DoublePoint(x/factor, y/factor)
  }

}


object Point {

  implicit def toIntPoint(tuple: (Int, Int)): IntPoint = IntPoint(tuple._1, tuple._2)
  implicit def toDoublePoint(tuple: (Double, Double)): DoublePoint = DoublePoint(tuple._1, tuple._2)

}