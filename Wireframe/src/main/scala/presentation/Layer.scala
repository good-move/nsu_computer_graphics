package presentation

import breeze.linalg.DenseMatrix
import data_layer.geometry.{Point2D, XRotationMatrix, YRotationMatrix}

import scala.collection.mutable.ListBuffer

class Layer() {

  private val initDeg = 90/180*math.Pi

  val splinePivots: ListBuffer[Point2D] = ListBuffer[Point2D]()


  var rotationMatrix: DenseMatrix[Double] = XRotationMatrix(initDeg).matrix * YRotationMatrix(initDeg).matrix
  var tmpRotationMatrix: DenseMatrix[Double] = XRotationMatrix(initDeg).matrix * YRotationMatrix(initDeg).matrix


  var translateMatrix: DenseMatrix[Double] = DenseMatrix.eye(4)
  var tmpTranslateMatrix: DenseMatrix[Double] = DenseMatrix.eye(4)


  var scaleMatrix: DenseMatrix[Double] = DenseMatrix.eye(4)
  var tmpScaleMatrix: DenseMatrix[Double] = DenseMatrix.eye(4)

}
