package data_layer.geometry

case class SimpleRange(start: Double, end: Double)


case class AngleRange(start: Double, end: Double) {
  private val MinValue = 0.0
  private val MaxValue = 2 * Math.PI

  if (start < MinValue || end > MaxValue) {
    throw new IllegalArgumentException(s"Range values must be within [$MinValue, $MaxValue]")
  }

}