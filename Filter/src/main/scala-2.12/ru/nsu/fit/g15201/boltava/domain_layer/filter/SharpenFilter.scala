package ru.nsu.fit.g15201.boltava.domain_layer.filter

import ru.nsu.fit.g15201.boltava.domain_layer.color.ColorHelpers
import ru.nsu.fit.g15201.boltava.domain_layer.filter.kernels.SharpenKernel

object SharpenFilter extends Transformer {


  override def transform(image: RawImage): RawImage = {
    val limit = (int: Int) => int.max(0).min(255)
    val resultConverter = (rgb: (Int, Int, Int)) => {
      ColorHelpers.intArgb(255, limit(rgb._1), limit(rgb._2), limit(rgb._3))
    }
    Filter.applyColored3by3Kernel(image, identity, resultConverter, SharpenKernel.matrix.toArray)
  }

}
