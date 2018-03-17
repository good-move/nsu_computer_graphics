package ru.nsu.fit.g15201.boltava.domain_layer.geometry

abstract class Point[T <: AnyVal](_x: T, _y: T) {
  def getX: T = _x
  def getY: T = _y
}

case class IntPoint(x: Int, y: Int) extends Point[Int](x, y)

case class DoublePoint(x: Double, y: Double) extends Point[Double](x, y)


object Point {

  implicit def toIntPoint(tuple: (Int, Int)): IntPoint = IntPoint(tuple._1, tuple._2)
  implicit def toDoublePoint(tuple: (Double, Double)): DoublePoint = DoublePoint(tuple._1, tuple._2)

}