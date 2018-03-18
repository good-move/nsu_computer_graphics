package ru.nsu.fit.g15201.boltava.domain_layer.filter

import ru.nsu.fit.g15201.boltava.domain_layer.color.ColorHelpers

object GrayScaleFilter extends Transformer {

  override def transform(image: RawImage): RawImage = {
    image.copy(_content = image.content.map { argb =>
      val alpha = ColorHelpers.alpha(argb)
      val intensity = ColorHelpers.intensity(argb)
      (alpha << 24) | (intensity << 16) | (intensity << 8) | intensity
    })
  }

}
