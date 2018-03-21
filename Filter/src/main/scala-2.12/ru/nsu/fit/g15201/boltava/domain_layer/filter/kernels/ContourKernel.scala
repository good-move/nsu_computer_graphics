package ru.nsu.fit.g15201.boltava.domain_layer.filter.kernels

object ContourKernel extends ThresholdKernel {

  private val _matrix = Seq(
    0,-1,0,
    -1,4,-1,
    0,-1,0
  )

  override def matrix: Seq[Int] = _matrix

  override def threshold: Int = 30

}
