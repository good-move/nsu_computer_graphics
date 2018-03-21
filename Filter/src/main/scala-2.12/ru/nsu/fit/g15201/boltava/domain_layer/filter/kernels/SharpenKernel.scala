package ru.nsu.fit.g15201.boltava.domain_layer.filter.kernels

object SharpenKernel extends FilterKernel {

  private val _matrix = Seq(
    0,-1,0,
    -1,5,-1,
    0,-1,0
  )

  override def matrix: Seq[Int] = _matrix

}
