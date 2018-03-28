package ru.nsu.fit.g15201.boltava.domain_layer.primitives

object ColorHelpers {

  private val alphaShift = 24
  private val redShift = 16
  private val greenShift = 8
  private val blueShift = 0
  private val onesByte = 0xff


  def intArgb(alpha: Int, red: Int, green: Int, blue: Int): Int = {
    (alpha << alphaShift) | (red << redShift) | (green << greenShift) | (blue << blueShift)
  }

}
