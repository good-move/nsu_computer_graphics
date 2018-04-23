package presentation

import breeze.linalg.{DenseMatrix, DenseVector}
import data_layer.geometry.Point2D
import scalafx.scene.canvas.Canvas
import scalafx.scene.input.MouseEvent
import scalafx.scene.layout.AnchorPane
import scalafxml.core.macros.sfxml
import scalafx.Includes._
import breeze.linalg._
import scalafx.scene.paint.Color

import scala.collection.mutable.ListBuffer

@sfxml
class Presenter(val wrapperPane: AnchorPane, val canvas: Canvas) {

  private val M: DenseMatrix[Double] = DenseMatrix(
    (-1.0,  3.0, -3.0,  1.0),
    ( 3.0, -6.0,  3.0,  0.0),
    (-3.0,  0.0,  3.0,  0.0),
    ( 1.0,  4.0,  1.0,  0.0)
  ) *:* (1.0/6)

  {
    canvas.height <== wrapperPane.height
    canvas.width <== wrapperPane.width
  }

  private val points = ListBuffer[Point2D]()
  private val q = 4
  private val pointRadius = 7

  def onClick(mouseEvent: MouseEvent): Unit = {
    val clickPoint = Point2D(mouseEvent.x, mouseEvent.y)

    points.find { case Point2D(x, y) =>
      math.pow(x-clickPoint.x, 2) + math.pow(y-clickPoint.y, 2) <= math.pow(pointRadius, 2)
    } match {
      case Some(Point2D(x, y)) =>
      case _ => addSplineSegment(clickPoint)
    }
  }

  private def addSplineSegment(point: Point2D): Unit = {
    points += point
    drawPoints()

    if (points.length >= q) {
      drawPoints(splineSegmentPoints())
    }
  }

  private def drawPoints(): Unit = {
    val gc = canvas.graphicsContext2D
    gc.fill = Color.Red
    points.foreach { point =>
      gc.fillOval(point.x, point.y, pointRadius, pointRadius)
    }
  }

  private def lastSplineSegmentPivots(): Seq[Point2D] = points.slice(points.length-q, points.length)

  private def splineSegmentPoints(segment: Seq[Point2D] = lastSplineSegmentPivots()): Seq[Point2D] = {
    val delta = 1 / (100.0 * points.length)
    val xVector = DenseVector(segment.map(_.x).toArray[Double])
    val yVector = DenseVector(segment.map(_.y).toArray[Double])

    for (t <- 0.0 until 1.0 by delta) yield {
      val tValues = DenseVector(t*t*t, t*t, t, 1)
      val x = tValues dot (M * xVector)
      val y = tValues dot (M * yVector)

      Point2D(x, y)
    }
  }

  private def drawPoints(points: Seq[Point2D]): Unit = {
    val gc = canvas.graphicsContext2D
    gc.stroke = Color.Black
    points.seq.sliding(2).foreach { case Seq(Point2D(x1, y1), Point2D(x2, y2)) =>
      gc.strokeLine(x1,y1,x2,y2)
    }
  }

  private def drawEntireSpline(): Unit = {
    val splinePoints = points.sliding(q).flatMap(splineSegmentPoints(_))
    drawPoints(splinePoints.toSeq)
  }

}
