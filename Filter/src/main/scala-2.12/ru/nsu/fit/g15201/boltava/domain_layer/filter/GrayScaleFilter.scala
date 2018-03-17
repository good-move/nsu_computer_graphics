package ru.nsu.fit.g15201.boltava.domain_layer.filter

object GrayScaleFilter extends Transformer {

  override def transform(image: RawImage): RawImage = {
    val maxByte = 0xff
    image.copy(_content = image.content.map { argb =>
      val alpha = (argb >> 24) & maxByte
      val red = (argb >> 16) & maxByte
      val green = (argb >> 8) & maxByte
      val blue = argb & maxByte
      val intensity = (0.299 * red + 0.578 * green + 0.114 * blue).ceil.toInt
      (alpha << 24) | (intensity << 16) | (intensity << 8) | intensity
    })
  }

}
