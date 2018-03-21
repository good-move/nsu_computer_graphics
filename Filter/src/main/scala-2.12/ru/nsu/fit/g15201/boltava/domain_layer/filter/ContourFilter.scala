package ru.nsu.fit.g15201.boltava.domain_layer.filter

import ru.nsu.fit.g15201.boltava.domain_layer.color.ColorHelpers
import ru.nsu.fit.g15201.boltava.domain_layer.filter.kernels.ContourKernel

object ContourFilter extends Transformer {
  override def transform(image: RawImage): RawImage = {
    val resultConverter = (value: Int) => {
      if (value >= ContourKernel.threshold) ColorHelpers.getWhite else ColorHelpers.getBlack
    }
    Filter.apply3by3Kernel(image, ColorHelpers.intensity, resultConverter, ContourKernel.matrix.toArray)
  }
}
