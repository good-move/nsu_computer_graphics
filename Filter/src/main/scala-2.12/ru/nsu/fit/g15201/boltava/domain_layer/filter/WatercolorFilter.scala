package ru.nsu.fit.g15201.boltava.domain_layer.filter

object WatercolorFilter {

  def apply(neighborsGridSize: Int): Transformer = new WatercolorFilter(neighborsGridSize)

}

class WatercolorFilter(neighborsGridSize: Int) extends Transformer {

  override def transform(image: RawImage): RawImage = {
    Transformable(image).transform(MedianFilter(neighborsGridSize), SharpenFilter).get
  }

}
