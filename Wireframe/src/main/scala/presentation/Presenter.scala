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

  private var lastClickedPointIndex: Int = -1
  private var existingPointClicked = false
  private var pointWasMoved = false

  def onPressed(mouseEvent: MouseEvent): Unit = {
    val clickPoint = Point2D(mouseEvent.x, mouseEvent.y)

    def pointsIntersect(p1: Point2D, p2: Point2D): Boolean =
      math.pow(p1.x-p2.x, 2) + math.pow(p1.y-p2.y, 2) <= math.pow(pointRadius, 2)

    lastClickedPointIndex = points.indexWhere { point => pointsIntersect(point, clickPoint) }
    existingPointClicked = lastClickedPointIndex >= 0
  }

  def onClick(mouseEvent: MouseEvent): Unit = {
    val clickPoint = Point2D(mouseEvent.x, mouseEvent.y)
    val pointToAdd = if (existingPointClicked) points(lastClickedPointIndex)
                      else clickPoint
    if (!pointWasMoved) {
      addAndDrawSplineSegment(pointToAdd)
    }
    pointWasMoved = false
  }

  def onDrag(mouseEvent: MouseEvent): Unit = {
    if (existingPointClicked) {
      points(lastClickedPointIndex) = Point2D(mouseEvent.x, mouseEvent.y)
      pointWasMoved = true
      cleanCanvas()
      val ps = splinePoints()
      drawPivots()
      drawPoints(ps)
    }
  }

  private def addAndDrawSplineSegment(point: Point2D): Unit = {
    points += point
    drawPivots()

    if (points.length >= q) {
      drawPoints(splineSegmentPoints())
    }
  }

  private def cleanCanvas(): Unit = {
    canvas.graphicsContext2D.clearRect(
      0,0,canvas.width.value, canvas.height.value
    )
  }

  private def drawPivots(): Unit = {
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

  private def splinePoints(): Seq[Point2D] =
    points.sliding(q).flatMap(splineSegmentPoints(_)).toSeq

}
