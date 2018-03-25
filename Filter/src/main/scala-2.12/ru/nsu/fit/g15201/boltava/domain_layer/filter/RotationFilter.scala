package ru.nsu.fit.g15201.boltava.domain_layer.filter

import ru.nsu.fit.g15201.boltava.domain_layer.color.ColorHelpers
import ru.nsu.fit.g15201.boltava.domain_layer.geometry.{DoublePoint, IntPoint}

object RotationFilter {

  def apply(angle: Int): Transformer = new RotationFilter(angle)

}

/**
  *
  * @param angle angle by which an image will be rotated. If angle > 0, the image
  *              is rotated clockwise, otherwise - anti-clockwise
  */
class RotationFilter(private val angle: Int) extends Transformer {

  override def transform(image: RawImage): RawImage = {
    val content = Array.ofDim[Int](image.width*image.height)
//    fillWhile(content)
    rotateBySampling(image, content)
  }

  private def fillWhile(content: Array[Int]): Unit = {
    for (i <- content.indices) {
      content(i) = ColorHelpers.getWhite
    }
  }

  def isPointInsideImage(source: RawImage, point: IntPoint): Boolean = {
    (0 <= point.x && point.x < source.width) &&
    (0 <= point.y && point.y < source.height)
  }

  private def rotateBySampling(image: RawImage, destination: Array[Int]): RawImage = {
    val cos = Math.cos(angle*Math.PI/180)
    val sin = Math.sin(-angle*Math.PI/180)
    val pivot: DoublePoint = (image.width/2.0, image.height/2.0)

    for (row <- 0 until image.height) {
      for (col <- 0 until image.width) {
        val xDiff = col + 0.5 - pivot.x
        val yDiff = row + 0.5 - pivot.y
        val x = xDiff*cos - yDiff*sin + pivot.x
        val y = xDiff*sin + yDiff*cos + pivot.y
        val xInt = x.toInt
        val yInt = y.toInt
        val color =
          if (isPointInsideImage(image, (xInt, yInt)))
            image.content(yInt*image.width + xInt)
          else
            ColorHelpers.getWhite
        destination(row*image.width + col) = color
      }
    }

    RawImage(
      image.width,
      image.height,
      destination
    )
  }

}
