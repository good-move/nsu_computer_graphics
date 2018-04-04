package ru.nsu.fit.g15201.boltava.domain_layer.logic.function

class EllipticHyperboloid extends Function2D(Domain2D(Range(NegativeInfinity, Infinity), Range(NegativeInfinity, Infinity))) {

  override def calculate(x: Double, y: Double): Double = Math.sqrt(1 + x*x + y*y)

  override def max: Double = {
    domain.map { d =>
      val maxX = d.xRange.start.value.abs.max(d.xRange.end.value.abs)
      val maxY = d.yRange.start.value.abs.max(d.yRange.end.value.abs)

      calculate(maxX, maxY)
    }.get
  }

  override def min: Double = {
    domain.map { d =>
      var domainConfiguration = 0
      val startX = d.xRange.start.value
      val endX = d.xRange.end.value
      val startY = d.yRange.start.value
      val endY = d.yRange.end.value

      if (startX > 0) domainConfiguration += 1
      if (startY > 0) domainConfiguration += 2
      if (endX < 0) domainConfiguration += 4
      if (endY < 0) domainConfiguration += 8

      domainConfiguration match {
        case 0 => calculate(0,0)

        case 1 => calculate(startX, 0)
        case 2 => calculate(endX, 0)

        case 4 => calculate(0, startY)
        case 8 => calculate(0, endY)

        case 5 => calculate(startX, startY)
        case 6 => calculate(endX, startY)
        case 9 => calculate(startX, endY)
        case 10 => calculate(endX, endY)

        case number => throw new RuntimeException(s"Invalid domain configuration: $number")
      }
    }.get
  }

}
