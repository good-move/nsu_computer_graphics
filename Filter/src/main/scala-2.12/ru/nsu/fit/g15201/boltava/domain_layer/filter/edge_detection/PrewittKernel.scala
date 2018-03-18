package ru.nsu.fit.g15201.boltava.domain_layer.filter.edge_detection

object PrewittKernel extends EdgeDetectionKernel {

  private val xKernel = Array(
    -1,-1,-1,
    0, 0, 0,
    1, 1, 1
  )

  private val yKernel = Array(
    -1, 0 ,1,
    -1, 0, 1,
    -1, 0, 1
  )

  override def xGradient: Array[Int] = xKernel

  override def yGradient: Array[Int] = yKernel

  override def threshold: Int = 50

}
