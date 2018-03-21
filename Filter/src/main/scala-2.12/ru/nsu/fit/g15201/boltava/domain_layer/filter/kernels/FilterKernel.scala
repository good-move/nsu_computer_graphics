package ru.nsu.fit.g15201.boltava.domain_layer.filter.kernels

trait FilterKernel {

  protected val default_size = 3

  def size: Int = default_size
  def matrix: Seq[Int]

}
