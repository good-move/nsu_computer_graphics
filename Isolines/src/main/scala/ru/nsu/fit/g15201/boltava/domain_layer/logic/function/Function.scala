package ru.nsu.fit.g15201.boltava.domain_layer.logic.function

trait Function[T <: Domain] {

  def domain: T
  def apply(x: Double, y: Double): Double

}

trait Function2D extends Function[Domain2D] {

  def domain: Domain2D
  def apply(x: Double, y: Double): Double

}
