package data_layer.geometry

import breeze.linalg.DenseMatrix
import breeze.linalg._

class ScaleMatrix(factor: Double) {
  val matrix = DenseMatrix(
    (factor, 0.0, 0.0, 0.0),
    (0.0, factor, 0.0, 0.0),
    (0.0, 0.0, factor, 0.0),
    (0.0,0.0, 0.0, 1.0)
  )
}

object ScaleMatrix {
  def apply(factor: Double): ScaleMatrix = new ScaleMatrix(factor)
}
