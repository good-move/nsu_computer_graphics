package presentation

import breeze.linalg.{DenseMatrix, DenseVector}
import data_layer.geometry._
import scalafx.scene.canvas.Canvas
import scalafx.scene.input._
import scalafx.scene.layout.AnchorPane
import scalafxml.core.macros.sfxml
import scalafx.Includes._
import breeze.linalg._
import breeze.numerics.{cos, sin}
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

object ViewMode extends Enumeration {
  type ViewMode = Value
  val Rotate, Move = Value
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
  private var viewMode = ViewMode.Rotate
  private var currentLayerIndex = 0

  private val splinePower = 4
  private val pointRadius = 9


  // Spline editing state
  private var lastClickedPointIndex: Int = -1
  private var existingPointClicked = false
  private var pointWasMoved = false
  private var isControlPressed = false

  private val controlKeyName = "Ctrl"


  private val angleCells = 20
  private val segmentCells = 10

  private val a: Double = 0.0
  private val b: Double = 0.9

  private val startAngle: Double = 0
  private val endAngle: Double = 2 * math.Pi

  private var dragAnchor = Point2D(.0,.0)
  private var scaleFactor = 1d

  {
    canvas.height <== wrapperPane.height - toolbar.height
    canvas.width <== wrapperPane.width

    canvas.width.onChange { (_,_, _) => redrawScene() }
    canvas.height.onChange { (_,_, _) => redrawScene() }

    canvas.onMouseReleased = (_) => {
      currentLayer.rotationMatrix = currentLayer.tmpRotationMatrix
      currentLayer.translateMatrix = currentLayer.tmpTranslateMatrix
    }

    canvas.onScroll = (scrollEvent: ScrollEvent) => {
      if (workingMode == WorkingMode.Viewing) {
        scaleFactor += (scrollEvent.deltaY / 1000)
        currentLayer.tmpScaleMatrix = ScaleMatrix(scaleFactor).matrix
        redrawScene()
      }
    }
  }

  // [START] ******************** Canvas Event Handlers ********************

  def onPressed(mouseEvent: MouseEvent): Unit = workingMode match {
    case WorkingMode.Editing =>
      val clickPoint = toSpaceCoordinates(Point2D(mouseEvent.x, mouseEvent.y))

      def pointsIntersect(p1: Point2D, p2: Point2D): Boolean =
        math.pow(p1.x-p2.x, 2) + math.pow(p1.y-p2.y, 2) <= math.pow(pointRadius, 2)

      lastClickedPointIndex = currentLayer.splinePivots.indexWhere { point => pointsIntersect(point, clickPoint) }
      existingPointClicked = lastClickedPointIndex >= 0
      if (isControlPressed && existingPointClicked) {
        currentLayer.splinePivots.remove(lastClickedPointIndex)
        existingPointClicked = false
        pointWasMoved = true
        redrawScene()
      }
    case WorkingMode.Viewing =>
      dragAnchor = toSpaceCoordinates(Point2D(mouseEvent.x, mouseEvent.y))
  }

  def onClick(mouseEvent: MouseEvent): Unit = workingMode match {
    case WorkingMode.Editing =>
      val clickPoint = toSpaceCoordinates(Point2D(mouseEvent.x, mouseEvent.y))
      val pointToAdd = if (existingPointClicked) currentLayer.splinePivots(lastClickedPointIndex)
                        else clickPoint
      if (!pointWasMoved && !existingPointClicked) {
        addAndDrawSplineSegment(pointToAdd)
      }
      pointWasMoved = false
    case WorkingMode.Viewing =>
  }

  def onDrag(mouseEvent: MouseEvent): Unit = workingMode match {
    case WorkingMode.Editing =>
      if (existingPointClicked) {
        currentLayer.splinePivots(lastClickedPointIndex) = toSpaceCoordinates(Point2D(mouseEvent.x, mouseEvent.y))
        pointWasMoved = true
        redrawScene()
      }
    case WorkingMode.Viewing =>
      val currentPoint = toSpaceCoordinates(Point2D(mouseEvent.x, mouseEvent.y))
      val dragDelta =  Point2D(currentPoint.x - dragAnchor.x, currentPoint.y - dragAnchor.y)

      viewMode match {
        case ViewMode.Rotate =>
          val xAngle = dragDelta.x / canvas.width.value * math.Pi
          val yAngle = dragDelta.y / canvas.height.value * math.Pi

          currentLayer.tmpRotationMatrix = currentLayer.rotationMatrix *
            XRotationMatrix(yAngle).matrix *
            YRotationMatrix(xAngle).matrix

        case ViewMode.Move =>
          val xShift = dragDelta.x / 2
          val yShift = dragDelta.y / 2
          currentLayer.tmpTranslateMatrix = currentLayer.translateMatrix * TranslateMatrix(xShift, yShift, 0).matrix
      }
      redrawScene()
  }

  // [START] ******************** Spline Editing ********************

  private def addAndDrawSplineSegment(point: Point2D): Unit = {
    currentLayer.splinePivots += point
    drawPivots()

    if (currentLayer.splinePivots.length >= splinePower) {
      redrawSpline()
    }
  }

  private def splinePoints(): Seq[Point2D] =
    currentLayer.splinePivots.sliding(splinePower).flatMap(splineSegmentPoints(_)).toSeq

  private def splineSegmentPivots(segmentIndex: Int): Seq[Point2D] = {
    val points = currentLayer.splinePivots
    points.slice(segmentIndex,segmentIndex + splinePower)
  }

  private def splineSegment(from: Int, to: Int): Seq[Seq[Point2D]] = {
    for (segmentIndex <- from to to) yield splineSegmentPivots(segmentIndex)
  }

  private def lastSplineSegmentPivots(): Seq[Point2D] = {
    val points = currentLayer.splinePivots
    points.slice(points.length-splinePower, points.length)
  }

  private def splineSegmentPoints(segment: Seq[Point2D] = lastSplineSegmentPivots(),
                                  tFrom: Double = 0d,
                                  tUntil: Double = 1d): Seq[Point2D] = {
    val delta = 1 / (25.0 * currentLayer.splinePivots.length)

    val (xVector, yVector) = segment match {
      case Seq(Point2D(x1, y1), Point2D(x2, y2), Point2D(x3, y3), Point2D(x4, y4)) =>
        (DenseVector(x1, x2, x3, x4), DenseVector(y1, y2, y3, y4))
    }


    for (t <- tFrom until tUntil by delta) yield {
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
    currentLayer.splinePivots.foreach { point =>
      val mappedPoint = toPixelCoordinates(point)
      gc.fillOval(mappedPoint.x, mappedPoint.y, pointRadius, pointRadius)
    }
  }

  private def redrawSpline(): Unit = {
    if (currentLayer.splinePivots.length >= splinePower) {
      drawPoints(splinePoints())
    }
  }

  private def redrawScene(): Unit = {
    cleanCanvas()
    drawAxis()

    workingMode match  {
      case WorkingMode.Editing =>
        drawPivots()
        redrawSpline()
      case WorkingMode.Viewing =>
        drawSolid()
    }

  }

  private def drawAxis(): Unit = {
    val gc = canvas.graphicsContext2D
    val width = canvas.width.value
    val height = canvas.height.value
    gc.strokeLine(width/2, 0, width/2, height)
    gc.strokeLine(0, height/2, width, height/2)
  }

  private def drawSolid(): Unit = {
    val m = currentLayer.splinePivots.length - splinePower
    val startSegmentIndex = (a * m).toInt
    val endSegmentIndex = (b * m).toInt
    val segments = splineSegment(startSegmentIndex, endSegmentIndex)

    val firstSegmentStart = a*m - (a*m).toInt
    val lastSegmentEnd = b*m - (b*m).toInt

    val fsPoints = splineSegmentPoints(segments.head, tFrom = firstSegmentStart)
    val lsPoints = splineSegmentPoints(segments.head, tUntil = lastSegmentEnd)

    val angleDelta =  (endAngle - startAngle) / angleCells

    val splinePointsSeq = segments.flatMap(splineSegmentPoints(_))


    val transform = currentLayer.tmpRotationMatrix * currentLayer.tmpTranslateMatrix * currentLayer.tmpScaleMatrix

    val shape = for (angle <- startAngle to endAngle by angleDelta) yield {
      splinePointsSeq.map { case Point2D(x, y) =>
        val X = cos(angle) * y
        val Y = sin(angle) * y
        val Z = x

        val p = transform * DenseVector(X, Y, Z, 1.0)
        Point2D(p(0), p(1))
      }
    }

    shape.foreach { segment => drawPoints(segment) }
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
    workingMode = WorkingMode.Editing
    redrawScene()
  }

  def onShowSolid(): Unit = {
    workingMode = WorkingMode.Viewing
    redrawScene()
  }

  def onShowAllSolids(): Unit = ???

  def onEnableMove(): Unit = {
    this.viewMode = ViewMode.Move
  }

  def onEnableRotate(): Unit = {
    this.viewMode = ViewMode.Rotate
  }

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
