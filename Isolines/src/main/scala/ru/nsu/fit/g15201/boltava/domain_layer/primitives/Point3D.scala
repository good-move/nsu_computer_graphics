package ru.nsu.fit.g15201.boltava.domain_layer.primitives

case class Point3D(x: Double, y: Double, z: Double) {

  def +(other: Point3D): Point3D = {
    Point3D(this.x + other.x, this.y + other.y, this.z + other.z)
  }

  def -(other: Point3D): Point3D = {
    Point3D(this.x - other.x, this.y - other.y, this.z - other.z)
  }

  def *(scalar: Double): Point3D = {
    Point3D(x * scalar, y * scalar, z * scalar)
  }

  def /(scalar: Double): Point3D = {
    Point3D(x / scalar, y / scalar, z / scalar)
  }

}

object Point3D {

  def tupleToPoint3D(tuple: (Double, Double, Double)): Point3D = Point3D(tuple._1, tuple._2, tuple._3)

}