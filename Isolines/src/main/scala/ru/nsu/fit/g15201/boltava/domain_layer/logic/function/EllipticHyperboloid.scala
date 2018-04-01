package ru.nsu.fit.g15201.boltava.domain_layer.logic.function

class EllipticHyperboloid extends Function2D(Domain2D(Range(NegativeInfinity, Infinity), Range(NegativeInfinity, Infinity))) {

  override def calculate(x: Double, y: Double): Double = Math.sqrt(1 + x*x + y*y)

}
