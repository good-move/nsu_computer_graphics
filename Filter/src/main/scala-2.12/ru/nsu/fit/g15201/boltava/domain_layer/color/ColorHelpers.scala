package ru.nsu.fit.g15201.boltava.domain_layer.color

object ColorHelpers {

  private val alphaShift = 24
  private val redShift = 16
  private val greenShift = 8
  private val blueShift = 0
  private val onesByte = 0xff

  private val white = 0xffffffff
  private val black = 0xff000000

  def alpha(color: Argb): Int = {
    (color.value >> alphaShift) & onesByte
  }

  def alpha(color: Int): Int = {
    (color >> alphaShift) & onesByte
  }

  def red(color: Argb): Int = {
    (color.value >> redShift) & onesByte
  }

  def red(color: Int): Int = {
    (color >> redShift) & onesByte
  }

  def green(color: Argb): Int = {
    (color.value >> greenShift) & onesByte
  }

  def green(color: Int): Int = {
    (color >> greenShift) & onesByte
  }

  def blue(color: Argb): Int = {
    (color.value >> blueShift) & onesByte
  }

  def blue(color: Int): Int = {
    (color >> blueShift) & onesByte
  }

  def intArgb(alpha: Int, red: Int, green: Int, blue: Int): Int = {
    (alpha << alphaShift) | (red << redShift) | (green << greenShift) | (blue << blueShift)
  }

  def getWhite: Int = white

  def getBlack: Int = black

  def intensity(color: Int): Int = {
    val alpha = this.alpha(color)
    val red = this.red(color)
    val green = this.green(color)
    val blue = this.blue(color)
    val intensity = (0.299 * red + 0.578 * green + 0.114 * blue).ceil.toInt
    intensity
  }

}
