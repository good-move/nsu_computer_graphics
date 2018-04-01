package ru.nsu.fit.g15201.boltava.domain_layer.logic.function

trait Function {

  def mathDomain: Domain2D
  def domain: Option[FiniteDomain2D]
  def domain_=(domain: FiniteDomain2D): Unit
  def apply(x: Double, y: Double): Double

}

trait IFunction2D extends Function
