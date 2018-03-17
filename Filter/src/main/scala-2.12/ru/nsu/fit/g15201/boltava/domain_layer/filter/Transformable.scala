package ru.nsu.fit.g15201.boltava.domain_layer.filter

object Transformable {

  def apply(image: RawImage): Transformable = new Transformable(image)

  implicit def imageToTransformable(image: RawImage): Transformable = {
    Transformable(image)
  }

}

class Transformable(private val image: RawImage) {

  def get: RawImage = image

  def transform(transformers: Transformer*): Transformable =
    transformers.foldLeft(image) { (im, transformer) =>
      transformer.transform(im)
    }

}
