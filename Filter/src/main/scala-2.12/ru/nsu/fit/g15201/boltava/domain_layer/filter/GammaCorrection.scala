package ru.nsu.fit.g15201.boltava.domain_layer.filter

import ru.nsu.fit.g15201.boltava.domain_layer.color.ColorHelpers


object GammaCorrection {
  def apply(gamma: Double): GammaCorrection = new GammaCorrection(gamma)
}

class GammaCorrection(gamma: Double) extends Transformer {

  override def transform(image: RawImage): RawImage = {
    val content = Array.ofDim[Int](image.width*image.height)

    for (index <- image.content.indices) {
      val pixel = image.content(index)
      val alpha = ColorHelpers.alpha(pixel)
      val red = gammaTransform(ColorHelpers.red(pixel))
      val green = gammaTransform(ColorHelpers.green(pixel))
      val blue = gammaTransform(ColorHelpers.blue(pixel))
      content(index) = ColorHelpers.intArgb(alpha, red, green, blue)
    }

    RawImage(
      image.width,
      image.height,
      content
    )
  }

  private def gammaTransform(luminosity: Int): Int = {
    ColorHelpers.clamp((Math.pow(luminosity/255.0, gamma) * 255).round.toInt)
  }

}
