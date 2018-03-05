package ru.nsu.fit.g15201.boltava.domain_layer.utils

object Extensions {

  implicit class DoubleExtension(private val double: Double) extends AnyVal {

    def within(lowerBound: Double, upperBound: Double): Boolean = {
      lowerBound <= double && double <=upperBound
    }

  }

}
