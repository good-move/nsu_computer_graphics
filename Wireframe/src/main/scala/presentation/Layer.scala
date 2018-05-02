package presentation

import breeze.linalg.DenseMatrix
import data_layer.geometry._
import data_layer.graphics.Color
import scalafx.scene.canvas.Canvas

import scala.collection.mutable.ListBuffer

class Layer(val canvas: Canvas, private val wireframe: WireFrame, index: Int) {

  private val initDeg = 90/180*math.Pi

  var angleCells: Int = 10
  var segmentCells: Int = 10
  val angleScaleFactor = 10d

  var _a: Double = wireframe.domain.segment.start
  def a: Double = _a
  def a_=(value: Double): Unit = {
    _a = value.max(0).min(0.9999)
  }

  var _b: Double = wireframe.domain.segment.end

  def b: Double = _b
  def  b_=(value: Double): Unit = {
    _b = value.max(0).min(0.9999)
  }

  var _startAngle: Double = 0

  {
    startAngle_=(wireframe.domain.angleRange.start)
  }

  def startAngle: Double = _startAngle
  def startAngle_=(angle: Double): Unit = {
    _startAngle = angle.max(0).min(2*math.Pi)
  }

  var _endAngle: Double = 0

  {
    endAngle_=(wireframe.domain.angleRange.end)
  }

  def endAngle: Double = _endAngle
  def endAngle_=(angle: Double): Unit = {
    _endAngle = angle.max(0).min(2*math.Pi)
  }

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
