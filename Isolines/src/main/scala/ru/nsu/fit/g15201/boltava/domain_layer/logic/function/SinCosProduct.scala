package ru.nsu.fit.g15201.boltava.domain_layer.logic.function

class SinCosProduct extends Function2D {

  override def domain: Domain2D = ???

  override def apply(x: Double, y: Double): Double = {
    if (domain.contains(x,y)) {
      Math.sin(x) * Math.cos(y)
    } else {
      throw DomainException("Point (x,y) doesn't belong to function domain")
    }
  }

}
