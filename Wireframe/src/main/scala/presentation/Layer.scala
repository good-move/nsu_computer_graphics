package presentation

import breeze.linalg.DenseMatrix
import data_layer.geometry._
import data_layer.settings.Config

import scala.collection.mutable.ListBuffer

class Layer(wireframe: WireFrame) {

  private val initDeg = 90/180*math.Pi

  val angleCells = 10
  val segmentCells = 10
  val angleScaleFactor = 10d

  var a: Double = wireframe.domain.segment.start
  var b: Double = wireframe.domain.segment.end

  val startAngle: Double = wireframe.domain.angleRange.start
  val endAngle: Double = wireframe.domain.angleRange.end

  var scaleFactor: Double = wireframe.domain.scaleFactor

  val splinePivots: ListBuffer[Point2D] = ListBuffer[Point2D](wireframe.spline: _*)

  var rotationMatrix: DenseMatrix[Double] = DenseMatrix.eye(4)
  var tmpRotationMatrix: DenseMatrix[Double] =
    XRotationMatrix(wireframe.rotationAngles._1).matrix *
    YRotationMatrix(wireframe.rotationAngles._2).matrix


  var translateMatrix: DenseMatrix[Double] = DenseMatrix.eye(4)

  var tmpTranslateMatrix: DenseMatrix[Double] = TranslateMatrix(
    wireframe.pivot.x,
    wireframe.pivot.y,
    wireframe.pivot.z
  ).matrix

  var scaleMatrix: DenseMatrix[Double] = DenseMatrix.eye(4)
  var tmpScaleMatrix: DenseMatrix[Double] = DenseMatrix.eye(4)
}
