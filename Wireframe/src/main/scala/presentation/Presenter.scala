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

  private var scene: Option[Scene] = None

  // General program state
  private val layers = Seq[Layer](new Layer())
  private var currentLayer = layers.head
  private var workingMode = WorkingMode.Editing
  private var currentLayerIndex = 0

  private val splinePower = 4
  private val pointRadius = 9


  // Spline editing state
  private var lastClickedPointIndex: Int = -1
  private var existingPointClicked = false
  private var pointWasMoved = false
  private var isControlPressed = false

  private val controlKeyName = "Ctrl"



  {
    canvas.height <== wrapperPane.height - toolbar.height
    canvas.width <== wrapperPane.width

    canvas.width.onChange { (_,_, _) => redrawScene() }
    canvas.height.onChange { (_,_, _) => redrawScene() }

  }

  // [START] ******************** Canvas Event Handlers ********************

  def onPressed(mouseEvent: MouseEvent): Unit = {
    val clickPoint = toSpaceCoordinates(Point2D(mouseEvent.x, mouseEvent.y))

    def pointsIntersect(p1: Point2D, p2: Point2D): Boolean =
      math.pow(p1.x-p2.x, 2) + math.pow(p1.y-p2.y, 2) <= math.pow(pointRadius, 2)

    lastClickedPointIndex = currentLayer.splinePoints.indexWhere { point => pointsIntersect(point, clickPoint) }
    existingPointClicked = lastClickedPointIndex >= 0
    if (isControlPressed && existingPointClicked) {
      currentLayer.splinePoints.remove(lastClickedPointIndex)
      existingPointClicked = false
      pointWasMoved = true
      redrawScene()
    }
  }

  def onClick(mouseEvent: MouseEvent): Unit = {
    val clickPoint = toSpaceCoordinates(Point2D(mouseEvent.x, mouseEvent.y))
    val pointToAdd = if (existingPointClicked) currentLayer.splinePoints(lastClickedPointIndex)
                      else clickPoint
    if (!pointWasMoved && !existingPointClicked) {
      addAndDrawSplineSegment(pointToAdd)
    }
    pointWasMoved = false
  }

  def onDrag(mouseEvent: MouseEvent): Unit = {
    if (existingPointClicked) {
      currentLayer.splinePoints(lastClickedPointIndex) = toSpaceCoordinates(Point2D(mouseEvent.x, mouseEvent.y))
      pointWasMoved = true
      redrawScene()
    }
  }

  // [START] ******************** Spline Editing ********************

  private def addAndDrawSplineSegment(point: Point2D): Unit = {
    currentLayer.splinePoints += point
    drawPivots()

    if (currentLayer.splinePoints.length >= splinePower) {
      redrawSpline()
    }
  }

  private def splinePoints(): Seq[Point2D] =
    currentLayer.splinePoints.sliding(splinePower).flatMap(splineSegmentPoints(_)).toSeq

  private def lastSplineSegmentPivots(): Seq[Point2D] = {
    val points = currentLayer.splinePoints
    points.slice(points.length-splinePower, points.length)
  }

  private def splineSegmentPoints(segment: Seq[Point2D] = lastSplineSegmentPivots()): Seq[Point2D] = {
    val delta = 1 / (100.0 * currentLayer.splinePoints.length)
    val xVector = DenseVector(segment.map(_.x).toArray[Double])
    val yVector = DenseVector(segment.map(_.y).toArray[Double])

    for (t <- 0.0 until 1.0 by delta) yield {
      val tValues = DenseVector(t*t*t, t*t, t, 1)
      val x = tValues dot (SplineMatrix * xVector)
      val y = tValues dot (SplineMatrix * yVector)

      Point2D(x, y)
    }
  }

  // [START] ******************** Canvas manipulation functions ********************

  private def cleanCanvas(): Unit = {
    canvas.graphicsContext2D.clearRect(
      0,0,canvas.width.value, canvas.height.value
    )
  }

  private def drawPoints(points: Seq[Point2D]): Unit = {
    val gc = canvas.graphicsContext2D
    gc.stroke = Color.Black
    points.seq.sliding(2).foreach { case Seq(p1, p2) =>
      val mapped1 = toPixelCoordinates(p1)
      val mapped2 = toPixelCoordinates(p2)
      gc.strokeLine(mapped1.x, mapped1.y, mapped2.x, mapped2.y)
    }
  }

  private def drawPivots(): Unit = {
    val gc = canvas.graphicsContext2D
    gc.fill = Color.Red
    currentLayer.splinePoints.foreach { point =>
      val mappedPoint = toPixelCoordinates(point)
      gc.fillOval(mappedPoint.x, mappedPoint.y, pointRadius, pointRadius)
    }
  }

  private def redrawSpline(): Unit = {
    if (currentLayer.splinePoints.length >= splinePower) {
      drawPoints(splinePoints())
    }
  }

  private def redrawScene(): Unit = {
    cleanCanvas()
    drawAxis()
    drawPivots()
    redrawSpline()
  }

  private def drawAxis(): Unit = {
    val gc = canvas.graphicsContext2D
    val width = canvas.width.value
    val height = canvas.height.value
    gc.strokeLine(width/2, 0, width/2, height)
    gc.strokeLine(0, height/2, width, height/2)
  }

  // [END] ******************** Canvas manipulation functions ********************


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

  private def onLayerIndexChanged(newIndex: Int): Unit = {
    currentLayerIndex = newIndex
    currentLayer = layers(newIndex)
  }


  private def toPixelCoordinates(point2D: Point2D): Point2D = point2D match {
    case Point2D(x, y) => Point2D(x + canvas.width.value / 2, canvas.height.value / 2 - y)
  }

  private def toSpaceCoordinates(point2D: Point2D): Point2D = point2D match {
    case Point2D(x, y) => Point2D(x - canvas.width.value / 2, canvas.height.value / 2 - y)
  }

}
