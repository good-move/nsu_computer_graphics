package ru.nsu.fit.g15201.boltava.domain_layer.filter.edge_detection

trait EdgeDetectionKernel {

  def xGradient: Array[Int]
  def yGradient: Array[Int]
  def threshold: Int
}
