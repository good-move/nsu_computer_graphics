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

object originVector {
  val vector = DenseVector(0d, 0d, 0d, 1d)
}

object iVector {
  val vector = DenseVector(1d, 0d, 0d, 1d)
}

object jVector {
  val vector = DenseVector(0d, 1d, 0d, 1d)
}

object kVector {
  val vector = DenseVector(0d, 0d, 1d, 1d)
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
  private val angleScaleFactor = 5d

  private val a: Double = 0.3
  private val b: Double = 0.7

  private val startAngle: Double = 0
  private val endAngle: Double = 2*math.Pi

  private var dragAnchor = Point2D(.0,.0)
  private var scaleFactor = 1d
  private var shouldDisplayWireframeBox = false

  {
    canvas.height <== wrapperPane.height - toolbar.height
    canvas.width <== wrapperPane.width

    canvas.width.onChange { (_,_, _) => redrawScene() }
    canvas.height.onChange { (_,_, _) => redrawScene() }

    canvas.onMouseReleased = _ => {
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
          val xShift = dragDelta.x
          val yShift = dragDelta.y
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
    currentLayer.splinePivots.slice(segmentIndex, segmentIndex + splinePower)
  }

  private def splineSegments(from: Int, to: Int): Seq[Seq[Point2D]] = {
    for (segmentIndex <- from to to) yield splineSegmentPivots(segmentIndex)
  }

  private def lastSplineSegmentPivots(): Seq[Point2D] = {
    val points = currentLayer.splinePivots
    points.slice(points.length-splinePower, points.length)
  }

  private def splineSegmentPoints(segment: Seq[Point2D] = lastSplineSegmentPivots(),
                                  tFrom: Double = 0d,
                                  tTo: Double = 1d): Seq[Point2D] = {
    val delta = 1 / 25.0

    val (xVector, yVector) = segment match {
      case Seq(Point2D(x1, y1), Point2D(x2, y2), Point2D(x3, y3), Point2D(x4, y4)) =>
        (DenseVector(x1, x2, x3, x4), DenseVector(y1, y2, y3, y4))
    }


    val tValues = DenseVector(1d, 1d, 1d, 1d)
    for (t <- (tFrom to tTo by delta).union(Seq(tTo))) yield {
      tValues(0) = t*t*t
      tValues(1) = t*t
      tValues(2) = t
      val x = tValues dot (SplineMatrix * xVector)
      val y = tValues dot (SplineMatrix * yVector)

      Point2D(x, y)
    }
  }

  private def createBoxPlanes(width: Double, minZ: Double, maxZ: Double): (Seq[Point3D], Seq[Point3D]) = {
    val topPlane = Seq(
      Point3D(width/2, width/2, minZ),
      Point3D(width/2, -width/2, minZ),
      Point3D(-width/2, -width/2, minZ),
      Point3D(-width/2, width/2, minZ),
      Point3D(width/2, width/2, minZ)
    )

    val bottomPlane = Seq(
      Point3D(width/2, width/2, maxZ),
      Point3D(width/2, -width/2, maxZ),
      Point3D(-width/2, -width/2, maxZ),
      Point3D(-width/2, width/2, maxZ),
      Point3D(width/2, width/2, maxZ)
    )

    (bottomPlane, topPlane)
  }

  // [START] ******************** Canvas manipulation functions ********************

  private def cleanCanvas(): Unit = {
    canvas.graphicsContext2D.clearRect(
      0,0,canvas.width.value, canvas.height.value
    )
  }

  private def drawPoints(points: Seq[Point2D], color: Color = Color.Black): Unit = {
    val gc = canvas.graphicsContext2D
    gc.stroke = color
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
        drawWireframe()
    }

  }

  private def drawAxis(): Unit = {
    val gc = canvas.graphicsContext2D
    val width = canvas.width.value
    val height = canvas.height.value
    gc.stroke = Color.Gray
    gc.strokeLine(width/2, 0, width/2, height)
    gc.strokeLine(0, height/2, width, height/2)
  }

  private def drawWireframe(): Unit = {
    val segmentsCount = currentLayer.splinePivots.length - splinePower + 1
    val leftBound = a * segmentsCount
    val rightBound = b * segmentsCount
    val firstSegmentIndex = leftBound.toInt
    val lastSegmentIndex = rightBound.toInt
    val firstSegmentStart = leftBound - firstSegmentIndex
    val lastSegmentEnd = rightBound - lastSegmentIndex

    val intermediateSegments = splineSegments(firstSegmentIndex+1, lastSegmentIndex-1)

    val splinePointsSeq = if (firstSegmentIndex != lastSegmentIndex) {
      val firstSegmentPoints = splineSegmentPoints(splineSegments(firstSegmentIndex, firstSegmentIndex).head, tFrom = firstSegmentStart)
      val lastSegmentPoints = splineSegmentPoints(splineSegments(lastSegmentIndex, lastSegmentIndex).head, tTo = lastSegmentEnd)
      firstSegmentPoints.union(intermediateSegments.flatMap(splineSegmentPoints(_))).union(lastSegmentPoints)
    } else {
      splineSegmentPoints(
        splineSegments(firstSegmentIndex, firstSegmentIndex).head,
        tFrom = firstSegmentStart,
        tTo = lastSegmentEnd
      )
    }

    val shapeTransform  = currentLayer.tmpTranslateMatrix * currentLayer.tmpRotationMatrix * currentLayer.tmpScaleMatrix
    val transformedVector = DenseVector(0d,0d,0d,1d)

    val angleDelta =  (endAngle - startAngle) / angleCells
    // Calculate vertical frame sets
    val verticalFrame = for (angle <- startAngle to endAngle by angleDelta) yield {
      splinePointsSeq.map { case Point2D(x, y) =>
        transformedVector(0) = cos(angle) * y
        transformedVector(1) = sin(angle) * y
        transformedVector(2) = -x
        val p = shapeTransform * transformedVector
        Point2D(p(0), p(1))
      }
    }
    verticalFrame.foreach { segment => drawPoints(segment) }

    // Calculate horizontal frame sets
    val segmentDelta = segmentsCount / segmentCells.toDouble
    val verticalTicks = for (t <- (leftBound to rightBound by segmentDelta).union(Seq(rightBound))) yield {
      val segmentIndex = t.toInt
      val parameterValue = t - segmentIndex
      splineSegmentPoints(
        splineSegments(segmentIndex, segmentIndex).head,
        parameterValue,
        parameterValue
      ).head
    }

    val angleScaledStep = angleDelta / angleScaleFactor

    val horizontalFrame = verticalTicks.map { case Point2D(x, y) =>
      for {
        angle <- startAngle to endAngle by angleDelta
        phi <- angle to (angle + angleDelta) by angleScaledStep
      } yield {
        transformedVector(0) = cos(phi) * y
        transformedVector(1) = sin(phi) * y
        transformedVector(2) = -x
        val p = shapeTransform * transformedVector
        Point2D(p(0), p(1))
      }
    }

    horizontalFrame.foreach { level => drawPoints(level) }

    if (shouldDisplayWireframeBox) {
      drawWireframeBox(splinePointsSeq, shapeTransform)
    }
    drawLocalAxis(shapeTransform)

  }

  private def drawLocalAxis(transform: DenseMatrix[Double], length: Double = 100d): Unit = {
    val shiftedOrigin = transform * originVector.vector

    def shiftVector(vector: DenseVector[Double]): DenseVector[Double] = {
      val result = transform * ScaleMatrix(length).matrix * vector
      if (result(3) != 1 && result(3) != 0) {
        result /= result(3)
      }
      result
    }

    val iShifted = shiftVector(iVector.vector)
    val jShifted = shiftVector(jVector.vector)
    val kShifted = shiftVector(kVector.vector)
    val origin = Point2D(shiftedOrigin(0), shiftedOrigin(1))

    drawPoints(Seq(origin, Point2D(iShifted(0), iShifted(1))), Color.Red)
    drawPoints(Seq(origin, Point2D(jShifted(0), jShifted(1))), Color.Green)
    drawPoints(Seq(origin, Point2D(kShifted(0), kShifted(1))), Color.Blue)
  }

  private def drawWireframeBox(splinePointsSeq: Seq[Point2D], transform: DenseMatrix[Double]): Unit = {
    // Shape Box dimensions
    var minZ = splinePointsSeq.head.x
    var maxZ = splinePointsSeq.last.x
    var maxL = 0d
    splinePointsSeq.foreach { case Point2D(x, y) =>
      if (x > maxZ) {
        maxZ = x
      }
      if (x < minZ) {
        minZ = x
      }
      if (maxL < y.abs) {
        maxL = y.abs
      }
    }

    val (bottomBoxPlane, topBoxPlane) = createBoxPlanes(width = 2 * maxL, -maxZ, -minZ)
    val transformedVector = DenseVector(0d,0d,0d,1d)

    def mapPlane(plane: Seq[Point3D]): Seq[Point2D] = plane.map { case Point3D(x, y, z) =>
      transformedVector(0) = x
      transformedVector(1) = y
      transformedVector(2) = z
      transformedVector(3) = 1d
      val p = transform * transformedVector
      Point2D(p(0), p(1))
    }

    val mappedTopPlane = mapPlane(topBoxPlane)
    val mappedBottomPlane = mapPlane(bottomBoxPlane)

    drawPoints(mappedTopPlane)
    drawPoints(mappedBottomPlane)
    for (i <- mappedBottomPlane.indices) {
      drawPoints(Seq(mappedTopPlane(i), mappedBottomPlane(i)))
    }
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

  def onShowWireframe(): Unit = {
    workingMode = WorkingMode.Viewing
    redrawScene()
  }

  def onShowAllWireframes(): Unit = ???

  def onEnableMove(): Unit = {
    this.viewMode = ViewMode.Move
  }

  def onEnableRotate(): Unit = {
    this.viewMode = ViewMode.Rotate
  }

  def onToggleBox(): Unit = {
    this.shouldDisplayWireframeBox = !this.shouldDisplayWireframeBox
    redrawScene()
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
