package ru.nsu.fit.g15201.boltava.domain_layer.logic.function

trait Domain

trait IDomain2D extends Domain {
  def contains(x: Double, y: Double): Boolean
}

case class Domain2D(xRange: Range, yRange: Range) extends IDomain2D {

  def contains(x: Double, y: Double): Boolean = {
    xRange.lower <= FiniteValue(x) && FiniteValue(x) <= xRange.upper &&
    yRange.lower <= FiniteValue(y) && FiniteValue(y) <= yRange.upper
  }

}

case class FiniteDomain2D(xRange: FiniteRange, yRange: FiniteRange) extends IDomain2D {

  override def contains(x: Double, y: Double): Boolean = {
    xRange.lower <= FiniteValue(x) && FiniteValue(x) <= xRange.upper &&
    yRange.lower <= FiniteValue(y) && FiniteValue(y) <= yRange.upper
  }

}
object FiniteDomain2D {

  def apply(xRangeStart: Double, xRangeEnd: Double, yRangeStart: Double, yRangeEnd: Double): FiniteDomain2D = {
    FiniteDomain2D(
      FiniteRange(FiniteValue(xRangeStart), FiniteValue(xRangeEnd)),
      FiniteRange(FiniteValue(yRangeStart), FiniteValue(yRangeEnd))
    )
  }

}


case class DomainException(message: String) extends Exception(message)
