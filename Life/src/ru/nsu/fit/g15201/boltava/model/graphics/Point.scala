package ru.nsu.fit.g15201.boltava.model.graphics

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



