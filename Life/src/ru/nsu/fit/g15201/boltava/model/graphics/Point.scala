package ru.nsu.fit.g15201.boltava.model.graphics

import java.util.function.DoubleBinaryOperator

class Point(private var _x: Int, private var _y: Int) {
  def x: Int = _x
  def x_=(x: Int): Unit = {
    this._x = x
  }

  def y: Int = _y
  def y_=(y: Int): Unit = {
    this._y = y
  }

  override def toString: String = s"(${_x}, ${_y})"
}

object Point {
  def apply(x: Int, y: Int): Point = new Point(x, y)

  implicit def tupleToPoint(tuple : (Int, Int)): Point = Point(tuple._1, tuple._2)
}

class DoublePoint(private var _x: Double, private var _y: Double) {
  def x: Double = _x
  def x_=(x: Double): Unit = {
    this._x = x
  }

  def y: Double = _y
  def y_=(y: Double): Unit = {
    this._y = y
  }

  override def toString: String = s"(${_x}, ${_y})"
}

object DoublePoint {
  def apply(x: Double, y: Double): DoublePoint = new DoublePoint(x, y)

  implicit def tupleToPoint(tuple : (Double, Double)): DoublePoint = DoublePoint(tuple._1, tuple._2)
}




