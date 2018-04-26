package data_layer.geometry

import breeze.linalg.DenseMatrix
import breeze.numerics.{cos, sin}
import breeze.linalg._


class XRotationMatrix(angle: Double) {
  val matrix = DenseMatrix(
    (1.0, 0.0, 0.0, 0.0),
    (0.0, cos(angle), -sin(angle), .0),
    (0.0, sin(angle), cos(angle), .0),
    (0.0, .0, .0, 1.0)
  )
}

object XRotationMatrix {

  def apply(angle: Double): XRotationMatrix = new XRotationMatrix(angle)

}

class YRotationMatrix(angle: Double) {
  val matrix = DenseMatrix(
    (cos(angle), .0, -sin(angle), .0),
    (.0,         1.0, .0,         .0),
    (sin(angle), .0,  cos(angle), .0),
    (.0,         .0,  .0,        1.0)
  )
}

object YRotationMatrix {

  def apply(angle: Double): YRotationMatrix = new YRotationMatrix(angle)

}


class ZRotationMatrix(angle: Double) {
  val matrix = DenseMatrix(
    (cos(angle), -sin(angle), .0, .0),
    (sin(angle), cos(angle), .0, .0),
    (.0, .0, 1.0, .0),
    (.0, .0, .0, 1.0)
  )
}

object ZRotationMatrix {

  def apply(angle: Double): ZRotationMatrix = new ZRotationMatrix(angle)

}