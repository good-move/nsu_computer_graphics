package presentation

import breeze.linalg.DenseMatrix
import data_layer.geometry._
import data_layer.graphics.Color
import scalafx.scene.canvas.Canvas

import scala.collection.mutable.ListBuffer

class Layer(val canvas: Canvas, private val wireframe: Option[WireFrame], val index: Int) {

  var angleCells: Int = 10
  var segmentCells: Int = 10
  val angleScaleFactor = 10d

  var _a: Double = wireframe.map(_.domain.segment.start).getOrElse(0)
  def a: Double = _a
  def a_=(value: Double): Unit = {
    _a = value.max(0).min(0.9999)
  }

  var _b: Double = wireframe.map(_.domain.segment.end).getOrElse(0.9999)

  def b: Double = _b
  def  b_=(value: Double): Unit = {
    _b = value.max(0).min(0.9999)
  }

  var _startAngle: Double = 0

  {
    startAngle_=(wireframe.map(_.domain.angleRange.start).getOrElse(0))
  }

  def startAngle: Double = _startAngle
  def startAngle_=(angle: Double): Unit = {
    _startAngle = angle.max(0).min(2*math.Pi)
  }

  var _endAngle: Double = 0

  {
    endAngle_=(wireframe.map(_.domain.angleRange.end).getOrElse(2*math.Pi))
  }

  def endAngle: Double = _endAngle
  def endAngle_=(angle: Double): Unit = {
    _endAngle = angle.max(0).min(2*math.Pi)
  }

  var zoomFactor: Double = 1
  var scaleFactor: Double = wireframe.map(_.domain.scaleFactor.toDouble).getOrElse(1.0)

  val splinePivots: ListBuffer[Point2D] = ListBuffer[Point2D](wireframe.map(_.spline).getOrElse(Seq()): _*)

  var rotationMatrix: DenseMatrix[Double] =
    XRotationMatrix(wireframe.map(_.rotationAngles._1).getOrElse(0)).matrix *
    YRotationMatrix(wireframe.map(_.rotationAngles._2).getOrElse(0)).matrix

  var translateMatrix: DenseMatrix[Double] = TranslateMatrix(
    wireframe.map(_.pivot.x).getOrElse(0),
    wireframe.map(_.pivot.y).getOrElse(0),
    wireframe.map(_.pivot.z).getOrElse(0)
  ).matrix

  var scaleMatrix: DenseMatrix[Double] = DenseMatrix.eye(4)

  val wireframeColor: Color = wireframe.map(_.color).getOrElse(Color(128, 128, 128))

  var needsRedraw: Boolean = true
  var visible: Boolean = false

  def label: String = s"Layer ${index+1}"

}
