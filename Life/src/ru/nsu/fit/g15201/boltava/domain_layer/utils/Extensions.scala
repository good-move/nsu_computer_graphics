package ru.nsu.fit.g15201.boltava.domain_layer.utils

object Extensions {

  implicit class DoubleExtension(double: Double) {

    def within(lowerBound: Double, upperBound: Double): Boolean = {
      lowerBound <= double && double <=upperBound
    }

  }

}
