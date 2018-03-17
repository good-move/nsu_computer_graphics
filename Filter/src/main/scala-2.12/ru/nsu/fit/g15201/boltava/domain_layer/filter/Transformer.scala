package ru.nsu.fit.g15201.boltava.domain_layer.filter

trait Transformer {

  def transform(image: RawImage): RawImage

}
