package ru.nsu.fit.g15201.boltava.domain_layer.filter

import ru.nsu.fit.g15201.boltava.domain_layer.color.ColorHelpers

object UniformScaling {

  def averageFilter(argb: Seq[Int]): Int = {
    val length = argb.length
    val summer = (fetcher: Int => Int) => (sum: Int, current: Int) => sum + fetcher(current)
    val alpha = argb.fold(0)(summer(ColorHelpers.alpha)) / length
    val red = argb.fold(0)(summer(ColorHelpers.red)) / length
    val green = argb.fold(0)(summer(ColorHelpers.green)) / length
    val blue = argb.fold(0)(summer(ColorHelpers.blue)) / length

    ColorHelpers.intArgb(alpha, red, green, blue)
  }

}
