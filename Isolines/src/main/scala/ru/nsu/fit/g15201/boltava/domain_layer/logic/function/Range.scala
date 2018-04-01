package ru.nsu.fit.g15201.boltava.domain_layer.logic.function

trait RangeValue {
  def <(other: RangeValue): Boolean
  def <=(other: RangeValue): Boolean
  def >(other: RangeValue): Boolean
  def >=(other: RangeValue): Boolean
  def ==(other: RangeValue): Boolean
  def !=(other: RangeValue): Boolean
}

trait IRange {
  def size: RangeValue
}

case class FiniteValue(value: Double) extends RangeValue {

  override def <(other: RangeValue): Boolean = other match {
    case Infinity => true
    case NegativeInfinity => false
    case FiniteValue(otherValue) => value < otherValue
  }

  override def <=(other: RangeValue): Boolean = other match {
    case Infinity => true
    case NegativeInfinity => false
    case FiniteValue(otherValue) => value <= otherValue
  }

  override def >(other: RangeValue): Boolean = other match {
    case Infinity => false
    case NegativeInfinity => true
    case FiniteValue(otherValue) => value > otherValue
  }

  override def >=(other: RangeValue): Boolean = other match {
    case Infinity => false
    case NegativeInfinity => true
    case FiniteValue(otherValue) => value >= otherValue
  }

  override def ==(other: RangeValue): Boolean = other match {
    case FiniteValue(otherValue) => otherValue == value
    case _ => false
  }

  override def !=(other: RangeValue): Boolean = !(this == other)
}

// A value that is greater than any other value
object Infinity extends RangeValue {

  override def <(other: RangeValue): Boolean = false

  override def <=(other: RangeValue): Boolean = false

  override def >(other: RangeValue): Boolean = true

  override def >=(other: RangeValue): Boolean = true

  override def ==(other: RangeValue): Boolean = {
    if (other.eq(this)) true
    else false
  }

  override def !=(other: RangeValue): Boolean = !(this == other)
}

// A value that is less than any other value
object NegativeInfinity extends RangeValue {

  override def <(other: RangeValue): Boolean = true

  override def <=(other: RangeValue): Boolean = true

  override def >(other: RangeValue): Boolean = false

  override def >=(other: RangeValue): Boolean = false

  override def ==(other: RangeValue): Boolean = {
    if (other.eq(this)) true
    else false
  }

  override def !=(other: RangeValue): Boolean = !(this == other)
}

case class Range(lower: RangeValue, upper: RangeValue) {

  def size: RangeValue = {
    lower match {
      case FiniteValue(lowerValue) => upper match {
        case FiniteValue(upperValue) => FiniteValue((upperValue-lowerValue).abs)
        case _ => Infinity
      }
      case _ => Infinity
    }
  }

}

case class FiniteRange(lower: FiniteValue, upper: FiniteValue) {
  def size: FiniteValue = FiniteValue((upper.value - lower.value).abs)
}