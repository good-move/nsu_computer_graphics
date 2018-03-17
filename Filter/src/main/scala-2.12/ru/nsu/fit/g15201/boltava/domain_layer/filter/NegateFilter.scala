package ru.nsu.fit.g15201.boltava.domain_layer.filter

object NegateFilter extends Transformer {

  override def transform(image: RawImage): RawImage = image.copy(_content = image.content map { argb =>
    val alpha = (argb >> 24) & 0xff
    val rgb = argb & 0x00ffffff
    (alpha << 24) | ~rgb
  })

}
