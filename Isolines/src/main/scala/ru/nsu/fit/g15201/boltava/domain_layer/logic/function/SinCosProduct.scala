package ru.nsu.fit.g15201.boltava.domain_layer.logic.function

class SinCosProduct extends Function2D(Domain2D(Range(NegativeInfinity, Infinity), Range(NegativeInfinity, Infinity))) {

  override def calculate(x: Double, y: Double): Double = Math.cos(x) * Math.sin(y)

  override def max: Double = 1

  override def min: Double = -1
}
