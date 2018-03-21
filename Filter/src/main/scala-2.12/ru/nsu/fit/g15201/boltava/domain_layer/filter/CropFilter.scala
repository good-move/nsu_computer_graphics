package ru.nsu.fit.g15201.boltava.domain_layer.filter

import ru.nsu.fit.g15201.boltava.domain_layer.geometry.IntPoint

object CropFilter {

  def apply(topLeft: IntPoint, bottomRight: IntPoint): CropFilter = new CropFilter(topLeft, bottomRight)
  def apply(topLeft: IntPoint, width: Int, height: Int): CropFilter = {
    if (width <= 0 || height <= 0) {
      throw new IllegalArgumentException("Width and Height of crop rectangle must be positive")
    }
    new CropFilter(topLeft, IntPoint(topLeft.x + width, topLeft.y + height))
  }

  def canCrop(topLeft: IntPoint, bottomRight: IntPoint, image: RawImage): Boolean = {
    isPointInsideImage(image, topLeft) && isPointInsideImage(image, bottomRight)
  }

  private def isPointInsideImage(image: Image, point: IntPoint): Boolean = {
    (0 <= point.x && point.x <= image.width) && (0 <= point.y && point.y <= image.height)
  }

}

class CropFilter(private val topLeft: IntPoint,
                 private val bottomRight: IntPoint
                ) extends Transformer {

  override def transform(image: RawImage): RawImage = {
    if (CropFilter.isPointInsideImage(image, topLeft) && CropFilter.isPointInsideImage(image, bottomRight)) {
      _transform(image)
    } else {
      throw new IllegalStateException("Crop rectangle is outside image dimensions")
    }
  }

  private def _transform(image: RawImage): RawImage = {
    val content = for {
      row <- topLeft.y until bottomRight.y
      col <- topLeft.x until bottomRight.x
    } yield image.content(row * image.width + col)

    RawImage(
      bottomRight.x - topLeft.x,
      bottomRight.y - topLeft.y,
      content.toArray
    )
  }

}
