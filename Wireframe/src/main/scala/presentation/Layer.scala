package presentation

import breeze.linalg.DenseMatrix
import data_layer.geometry._
import data_layer.graphics.Color
import data_layer.settings.Config
import scalafx.scene.canvas.Canvas

import scala.collection.mutable.ListBuffer

class Layer(val canvas: Canvas, private val wireframe: WireFrame, index: Int) {

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

  var rotationMatrix: DenseMatrix[Double] =
    XRotationMatrix(wireframe.rotationAngles._1).matrix *
    YRotationMatrix(wireframe.rotationAngles._2).matrix

  var translateMatrix: DenseMatrix[Double] = TranslateMatrix(
    wireframe.pivot.x,
    wireframe.pivot.y,
    wireframe.pivot.z
  ).matrix

  var scaleMatrix: DenseMatrix[Double] = DenseMatrix.eye(4)

  val wireframeColor: Color = wireframe.color

  var needsRedraw: Boolean = true
  var visible: Boolean = false

  def label: String = s"Layer ${index+1}"

}
