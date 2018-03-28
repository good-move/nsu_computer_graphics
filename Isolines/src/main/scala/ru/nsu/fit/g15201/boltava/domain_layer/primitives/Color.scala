package ru.nsu.fit.g15201.boltava.domain_layer.primitives

case class Color(color: Int) {
  def this(red: Int, green: Int, blue: Int) = {
    this(ColorHelpers.intArgb(255, red, green, blue))
  }

}
