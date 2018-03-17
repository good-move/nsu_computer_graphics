package ru.nsu.fit.g15201.boltava.domain_layer.filter

case class RawImage(
                    private val _width: Int,
                    private val _height: Int,
                    private val _content: Array[Int]) extends Image {

  def width: Int = _width

  def height: Int = _height

  override def content: Array[Int] = _content

}

