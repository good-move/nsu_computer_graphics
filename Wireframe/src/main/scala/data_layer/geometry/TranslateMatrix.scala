package data_layer.geometry

import breeze.linalg.DenseMatrix

class TranslateMatrix(a: Double, b: Double, c: Double) {
  val matrix = DenseMatrix(
    (1.0, 0.0, 0.0, a),
    (0.0, 1.0, 0.0, b),
    (0.0, 0.0, 1.0, c),
    (0.0,0.0, 0.0, 1.0)
  )
}


object TranslateMatrix {
  def apply(a: Double, b: Double, c: Double): TranslateMatrix = new TranslateMatrix(a,b,c)
}