package presentation

import breeze.linalg.{DenseMatrix, DenseVector}
import data_layer.geometry.Point2D
import scalafx.scene.canvas.Canvas
import scalafx.scene.input.{KeyEvent, MouseEvent}
import scalafx.scene.layout.AnchorPane
import scalafxml.core.macros.sfxml
import scalafx.Includes._
import breeze.linalg._
import scalafx.scene.Scene
import scalafx.scene.control.ToolBar
import scalafx.scene.paint.Color


// TODO: Change cursor when hovering anchor points
// TODO: Add Undo function
// TODO: Add ability to select multiple pivots and move them simultaneously

object WorkingMode extends Enumeration {
  type WorkingMode = Value
  val Editing, Viewing = Value
}

@sfxml
class Presenter(val wrapperPane: AnchorPane, val toolbar: ToolBar, val canvas: Canvas) extends IPresenter {

  private val SplineMatrix: DenseMatrix[Double] = DenseMatrix(
    (-1.0,  3.0, -3.0,  1.0),
    ( 3.0, -6.0,  3.0,  0.0),
    (-3.0,  0.0,  3.0,  0.0),
    ( 1.0,  4.0,  1.0,  0.0)
  ) *:* (1.0/6)

  {
    canvas.height <== wrapperPane.height - toolbar.height
    canvas.width <== wrapperPane.width
  }

  private var scene: Option[Scene] = None

  // General program state
  private val layers = Seq[Layer](new Layer())
  private var workingMode = WorkingMode.Editing
  private var currentLayer = 0

  private val splinePower = 4
  private val pointRadius = 9


  // Spline editing state
  private var lastClickedPointIndex: Int = -1
  private var existingPointClicked = false
  private var pointWasMoved = false
  private var isControlPressed = false

  private val controlKeyName = "Ctrl"


  def onPressed(mouseEvent: MouseEvent): Unit = {
    val clickPoint = Point2D(mouseEvent.x, mouseEvent.y)

    def pointsIntersect(p1: Point2D, p2: Point2D): Boolean =
      math.pow(p1.x-p2.x, 2) + math.pow(p1.y-p2.y, 2) <= math.pow(pointRadius, 2)

    val currentLayerr = layers(currentLayer)

    lastClickedPointIndex = currentLayerr.splinePoints.indexWhere { point => pointsIntersect(point, clickPoint) }
    existingPointClicked = lastClickedPointIndex >= 0
    if (isControlPressed && existingPointClicked) {
      currentLayerr.splinePoints.remove(lastClickedPointIndex)
      existingPointClicked = false
      pointWasMoved = true
      redrawScene()
    }
  }

  def onClick(mouseEvent: MouseEvent): Unit = {
    val clickPoint = Point2D(mouseEvent.x, mouseEvent.y)
    val pointToAdd = if (existingPointClicked) layers(currentLayer).splinePoints(lastClickedPointIndex)
                      else clickPoint
    if (!pointWasMoved && !existingPointClicked) {
      addAndDrawSplineSegment(pointToAdd)
    }
    pointWasMoved = false
  }

  def onDrag(mouseEvent: MouseEvent): Unit = {
    if (existingPointClicked) {
      layers(currentLayer).splinePoints(lastClickedPointIndex) = Point2D(mouseEvent.x, mouseEvent.y)
      pointWasMoved = true
      redrawScene()
    }
  }

  private def addAndDrawSplineSegment(point: Point2D): Unit = {
    layers(currentLayer).splinePoints += point
    drawPivots()

    if (layers(currentLayer).splinePoints.length >= splinePower) {
      redrawSpline()
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
    layers(currentLayer).splinePoints.foreach { point =>
      gc.fillOval(point.x, point.y, pointRadius, pointRadius)
    }
  }

  private def lastSplineSegmentPivots(): Seq[Point2D] = {
    val points = layers(currentLayer).splinePoints
    points.slice(points.length-splinePower, points.length)
  }

  private def splineSegmentPoints(segment: Seq[Point2D] = lastSplineSegmentPivots()): Seq[Point2D] = {
    val delta = 1 / (100.0 * layers(currentLayer).splinePoints.length)
    val xVector = DenseVector(segment.map(_.x).toArray[Double])
    val yVector = DenseVector(segment.map(_.y).toArray[Double])

    for (t <- 0.0 until 1.0 by delta) yield {
      val tValues = DenseVector(t*t*t, t*t, t, 1)
      val x = tValues dot (SplineMatrix * xVector)
      val y = tValues dot (SplineMatrix * yVector)

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
    layers(currentLayer).splinePoints.sliding(splinePower).flatMap(splineSegmentPoints(_)).toSeq

  private def redrawSpline(): Unit = {
    if (layers(currentLayer).splinePoints.length >= splinePower) {
      drawPoints(splinePoints())
    }
  }

  private def redrawScene(): Unit = {
    cleanCanvas()
    drawPivots()
    redrawSpline()
  }

  override def setScene(scene: Scene): Unit = {
    this.scene = Some(scene)
    setKeyListeners()
  }

  private def setKeyListeners(): Unit = {
    scene.foreach( _.onKeyPressed = (event: KeyEvent) => {
      isControlPressed = event.controlDown
    })

    scene.foreach(_.onKeyReleased = (event: KeyEvent) => {
      if (controlKeyName == event.code.name) {
        isControlPressed = false
      }
    })
  }

  def onEditBaseline(): Unit = {

  }

  def onShowSolid(): Unit = {

  }

  def onShowAllSolids(): Unit = ???

}
