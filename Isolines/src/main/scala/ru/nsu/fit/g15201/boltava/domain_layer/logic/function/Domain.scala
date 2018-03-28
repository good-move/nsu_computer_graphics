package ru.nsu.fit.g15201.boltava.domain_layer.logic.function

case class Range(left: Double, right: Double)


trait Domain

case class Domain2D(xRange: Range, yRange: Range) extends Domain {

  def contains(x: Double, y: Double): Boolean = {
    xRange.left <= x && x <= xRange.right &&
    yRange.left <= y && y <= yRange.right
  }

}

case class DomainException(message: String) extends Exception(message)