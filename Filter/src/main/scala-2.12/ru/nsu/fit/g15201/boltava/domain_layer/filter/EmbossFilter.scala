package ru.nsu.fit.g15201.boltava.domain_layer.filter

import ru.nsu.fit.g15201.boltava.domain_layer.color.ColorHelpers
import ru.nsu.fit.g15201.boltava.domain_layer.filter.kernels.EmbossKernel

object EmbossFilter extends Transformer {

  override def transform(image: RawImage): RawImage = {
    val resultConverter = (value: Int) => {
      val shiftedValue = value+128
      val color = shiftedValue.max(0).min(255)
      ColorHelpers.intArgb(255, color, color, color)
    }
    Filter.apply3by3Kernel(image, ColorHelpers.intensity, resultConverter, EmbossKernel.matrix.toArray)
  }

}
