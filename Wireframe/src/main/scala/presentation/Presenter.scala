package presentation

import breeze.linalg.{DenseMatrix, DenseVector}
import data_layer.geometry._
import scalafx.scene.canvas.Canvas
import scalafx.scene.input._
import scalafx.scene.layout.{AnchorPane, StackPane, VBox}
import scalafxml.core.macros.sfxml
import scalafx.Includes._
import breeze.linalg._
import breeze.numerics.{cos, sin}
import data_layer.settings.Config
import scalafx.collections.ObservableBuffer
import scalafx.scene.Scene
import scalafx.scene.control.{ListView, TextField, ToolBar}
import scalafx.scene.paint.Color

import scala.collection.mutable
import scala.collection.mutable.ListBuffer


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
class Presenter(val wrapperPane: AnchorPane,
                val toolbar: ToolBar,
                val rootCanvas: Canvas,
                val canvasStack: StackPane,
                val layersList: ListView[Layer],
                val segmentStartTF: TextField,
                val segmentEndTF: TextField,
                val segmentCellsTF: TextField,
                val angleStartTF: TextField,
                val angleEndTF: TextField,
                val angleCellsTF: TextField,
                val redTF: TextField,
                val greenTF: TextField,
                val blueTF: TextField,
                val settingsPane: VBox,
                val config: Config) extends IPresenter {

  private val SplineMatrix: DenseMatrix[Double] = DenseMatrix(
    (-1.0,  3.0, -3.0,  1.0),
    ( 3.0, -6.0,  3.0,  0.0),
    (-3.0,  0.0,  3.0,  0.0),
    ( 1.0,  4.0,  1.0,  0.0)
  ) *:* (1.0/6)

  private var scene: Option[Scene] = None

  // General program state
  private val layers = ListBuffer[Layer](config.wireframes.indices.map { index =>
    val canvas = new Canvas()
    configureCanvas(canvas)
    canvasStack.children.add(canvas)
    new Layer(canvas, Some(config.wireframes(index)), index)
  }: _*)

  private var currentLayerIndex = layers.length - 1
  private var currentLayer = layers(currentLayerIndex)
  private var workingMode = WorkingMode.Editing
  private var viewMode = ViewMode.Rotate

  private val splinePower = 4
  private val pointRadius = 9

  private val controlKeyName = "Ctrl"

  // Spline editing state
  private var lastClickedPointIndex: Int = -1
  private var existingPointClicked = false
  private var pointWasMoved = false
  private var isControlPressed = false

  private var dragAnchor = Point2D(.0,.0)
  private var shouldDisplayWireframeBox = false

  private val visibleLayers: mutable.Set[Int] = mutable.Set[Int]()

  // constructor
  {
    visibleLayers.add(currentLayerIndex)
    currentLayer.visible = true

    configureCanvas(rootCanvas)
    configureLayersList()
    redrawLayers()


    segmentStartTF.text.onChange((_, _, newValue: String) => {
      currentLayer.a = newValue.toDouble
      redrawLayer(currentLayer)
    })

    segmentEndTF.text.onChange((_, _, newValue: String) => {
      currentLayer.b = newValue.toDouble
      redrawLayer(currentLayer)
    })

    angleStartTF.text.onChange((_, _, newValue: String) => {
      currentLayer.startAngle = newValue.toDouble
      redrawLayer(currentLayer)
    })

    angleEndTF.text.onChange((_, _, newValue: String) => {
      currentLayer.endAngle = newValue.toDouble
      redrawLayer(currentLayer)
    })

    segmentCellsTF.text.onChange((_, _, newValue: String) => {
      currentLayer.segmentCells = newValue.toInt
      redrawLayer(currentLayer)
    })

    angleCellsTF.text.onChange((_, _, newValue: String) => {
      currentLayer.angleCells = newValue.toInt
      redrawLayer(currentLayer)
    })

  }

  private def configureCanvas(canvas: Canvas): Unit = {

    canvas.height <== wrapperPane.height - toolbar.height - settingsPane.height
    canvas.width <== wrapperPane.width - layersList.width

    canvas.width.onChange { (_,_, _) => {
      layers.foreach(_.needsRedraw = true)
      redrawLayers()
    } }

    canvas.height.onChange { (_,_, _) => {
      layers.foreach(_.needsRedraw = true)
      redrawLayers()
    } }

    canvas.onMousePressed = this.onPress
    canvas.onMouseReleased = this.onRelease
    canvas.onMouseClicked = this.onClick
    canvas.onMouseDragged = this.onDrag
    canvas.onScroll = this.onScroll
  }

  private def configureLayersList(): Unit = {
    setListItems()

    layersList.cellFactory = { list =>
      val cell = new Cell()

      cell.onMouseClicked = _ => {
        this.onLayerIndexChanged(list.items.value.size() - 1 - cell.getIndex)
      }

      cell.checkBox.onMouseClicked = _ => {
        this.toggleLayerVisibility(list.items.value.size() - 1 - cell.getIndex)
      }

      cell.removeButton.onMouseClicked = _ => {
        removeLayer(list.items.value.size() - 1 - cell.getIndex)
      }

      cell
    }
  }

  private def setListItems(): Unit = {
    layersList.items = ObservableBuffer[Layer](layers.reverse)
  }

  // [START] ******************** Canvas Event Handlers ********************

  private def onScroll(scrollEvent: ScrollEvent): Unit = {
    if (workingMode == WorkingMode.Viewing) {
      currentLayer.zoomFactor += (scrollEvent.deltaY / 1000)
      currentLayer.scaleMatrix = ScaleMatrix(currentLayer.zoomFactor).matrix
      currentLayer.needsRedraw = true
      redrawLayers()
    }
  }

  private def onRelease(mouseEvent: MouseEvent): Unit = {
    currentLayer.translateMatrix = currentLayer.translateMatrix
  }

  private def onPress(mouseEvent: MouseEvent): Unit = workingMode match {
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
        redrawLayers()
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
        redrawLayers()
      }
    case WorkingMode.Viewing =>
      val currentPoint = toSpaceCoordinates(Point2D(mouseEvent.x, mouseEvent.y))
      val dragDelta =  Point2D(currentPoint.x - dragAnchor.x, currentPoint.y - dragAnchor.y)

      viewMode match {
        case ViewMode.Rotate =>
          val xAngle = dragDelta.x / currentLayer.canvas.width.value * math.Pi
          val yAngle = dragDelta.y / currentLayer.canvas.height.value * math.Pi

          if (isControlPressed) {
            currentLayer.rotationMatrix = currentLayer.rotationMatrix * ZRotationMatrix(xAngle).matrix
          } else {
            currentLayer.rotationMatrix = currentLayer.rotationMatrix *
              XRotationMatrix(yAngle).matrix *
              YRotationMatrix(xAngle).matrix

          }

        case ViewMode.Move =>
          val xShift = dragDelta.x
          val yShift = dragDelta.y
          currentLayer.translateMatrix = currentLayer.translateMatrix * TranslateMatrix(xShift, yShift, 0).matrix
      }
      currentLayer.needsRedraw = true
      dragAnchor = currentPoint
      redrawLayers()
  }

  def onClear(): Unit = {
    if (workingMode == WorkingMode.Editing) {
      currentLayer.splinePivots.clear()
      redrawLayers()
    }
  }

  // [START] ******************** Spline Editing ********************

  private def addAndDrawSplineSegment(point: Point2D): Unit = {
    currentLayer.splinePivots += point
    drawPivots()

    if (currentLayer.splinePivots.length >= splinePower) {
      redrawSpline()
    }
  }

  private def splinePoints(layer: Layer): Seq[Point2D] =
    layer.splinePivots.sliding(splinePower).flatMap(splineSegmentPoints(_)).toSeq

  private def splineSegmentPivots(layer: Layer, segmentIndex: Int): Seq[Point2D] = {
    layer.splinePivots.slice(segmentIndex, segmentIndex + splinePower)
  }

  private def splineSegments(layer: Layer, from: Int, to: Int): Seq[Seq[Point2D]] = {
    for (segmentIndex <- from to to) yield splineSegmentPivots(layer, segmentIndex)
  }

  private def lastSplineSegmentPivots(layer: Layer = currentLayer): Seq[Point2D] = {
    val points = layer.splinePivots
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

  private def cleanCanvas(canvas: Canvas): Unit = {
    canvas.graphicsContext2D.clearRect(
      0, 0, canvas.width.value, canvas.height.value
    )
  }

  private def drawPoints(points: Seq[Point2D], color: Color = Color.Black): Unit = {
    val canvas = currentLayer.canvas
    val gc = canvas.graphicsContext2D
    gc.stroke = color
    points.seq.sliding(2).foreach { case Seq(p1, p2) =>
      val mapped1 = toPixelCoordinates(p1)
      val mapped2 = toPixelCoordinates(p2)
      gc.strokeLine(mapped1.x, mapped1.y, mapped2.x, mapped2.y)
    }
  }

  private def drawPivots(): Unit = {
    val canvas = currentLayer.canvas
    val gc = canvas.graphicsContext2D
    gc.fill = Color.Red
    currentLayer.splinePivots.foreach { point =>
      val mappedPoint = toPixelCoordinates(point)
      gc.fillOval(mappedPoint.x, mappedPoint.y, pointRadius, pointRadius)
    }
  }

  private def redrawSpline(): Unit = {
    if (currentLayer.splinePivots.length >= splinePower) {
      drawPoints(splinePoints(currentLayer))
    }
  }

  private def redrawLayers(): Unit = {
    cleanCanvas(rootCanvas)
    drawAxis()

    workingMode match  {
      case WorkingMode.Editing =>
        cleanCanvas(currentLayer.canvas)
        drawPivots()
        redrawSpline()
      case WorkingMode.Viewing =>
        for (index <- visibleLayers) {
          val layer = layers(index)

          if (layer.needsRedraw) {
            redrawLayer(layer)
            layer.needsRedraw = false
          }
        }
    }

  }

  private def redrawLayer(layer: Layer): Unit = {
    cleanCanvas(layer.canvas)
    drawWireframe(layer)
  }

  private def drawAxis(): Unit = {
    rootCanvas.visible = true
    val canvas = rootCanvas
    val gc = canvas.graphicsContext2D
    val width = canvas.width.value
    val height = canvas.height.value
    gc.stroke = Color.Gray
    gc.strokeLine(width/2, 0, width/2, height)
    gc.strokeLine(0, height/2, width, height/2)
  }

  private def drawWireframe(layer: Layer): Unit = {
    val segmentsCount = layer.splinePivots.length - splinePower + 1
    val leftBound = layer.a * segmentsCount
    val rightBound = layer.b * segmentsCount
    val firstSegmentIndex = leftBound.toInt
    val lastSegmentIndex = rightBound.toInt
    val firstSegmentStart = leftBound - firstSegmentIndex
    val lastSegmentEnd = rightBound - lastSegmentIndex

    val intermediateSegments = splineSegments(layer, firstSegmentIndex+1, lastSegmentIndex-1)

    val splinePointsSeq = if (firstSegmentIndex != lastSegmentIndex) {
      val firstSegmentPoints = splineSegmentPoints(splineSegments(layer, firstSegmentIndex, firstSegmentIndex).head, tFrom = firstSegmentStart)
      val lastSegmentPoints = splineSegmentPoints(splineSegments(layer, lastSegmentIndex, lastSegmentIndex).head, tTo = lastSegmentEnd)
      firstSegmentPoints ++ intermediateSegments.flatMap(splineSegmentPoints(_)) ++ lastSegmentPoints
    } else {
      splineSegmentPoints(
        splineSegments(layer, firstSegmentIndex, firstSegmentIndex).head,
        tFrom = firstSegmentStart,
        tTo = lastSegmentEnd
      )
    }

    val shapeTransform  = layer.translateMatrix * layer.rotationMatrix * layer.scaleMatrix
    val transformedVector = DenseVector(0d,0d,0d,1d)


    val startAngle = layer.startAngle
    val endAngle = layer.endAngle
    val angleDelta =  (endAngle - startAngle) / layer.angleCells
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

    val wireframeColor = Color.rgb(
      layer.wireframeColor.red,
      layer.wireframeColor.green,
      layer.wireframeColor.blue
    )
    verticalFrame.foreach { segment => drawPoints(segment, wireframeColor) }

    // Calculate horizontal frame sets
    val segmentDelta = segmentsCount / layer.segmentCells.toDouble
    val verticalTicks = for (t <- (leftBound to rightBound by segmentDelta).union(Seq(rightBound))) yield {
      val segmentIndex = t.toInt
      val parameterValue = t - segmentIndex
      splineSegmentPoints(
        splineSegments(layer, segmentIndex, segmentIndex).head,
        parameterValue,
        parameterValue
      ).head
    }

    val angleScaledStep = angleDelta / layer.angleScaleFactor

    val horizontalFrame = verticalTicks.map { case Point2D(x, y) =>
      for {
        angle <- startAngle to endAngle by angleDelta
        phi <- angle to (angle + angleDelta) by angleScaledStep if phi <= endAngle
      } yield {
        transformedVector(0) = cos(phi) * y
        transformedVector(1) = sin(phi) * y
        transformedVector(2) = -x
        val p = shapeTransform * transformedVector
        Point2D(p(0), p(1))
      }
    }

    horizontalFrame.foreach { level => drawPoints(level, wireframeColor) }

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
    layers.foreach(_.canvas.visible = false)
    currentLayer.canvas.visible = true
    currentLayer.needsRedraw = true

    redrawLayers()
  }

  def onShowWireframe(): Unit = {
    workingMode = WorkingMode.Viewing
    layers.foreach(_.canvas.visible = false)
    visibleLayers.foreach(layers(_).canvas.visible = true)

    redrawLayers()
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
    redrawLayers()
  }

  private def onLayerIndexChanged(newIndex: Int): Unit = {
    // TODO: fix for camera view
    if (newIndex != currentLayerIndex) {
      val oldLayerIndex = currentLayerIndex
      currentLayerIndex = newIndex
      if (0 <= currentLayerIndex && currentLayerIndex < layers.length) {
        currentLayer = layers(newIndex)
        if (workingMode == WorkingMode.Editing) {
          layers(oldLayerIndex).canvas.visible = false
          currentLayer.canvas.visible = true
          currentLayer.needsRedraw = true
          redrawLayers()
        }
      } else {
        drawAxis()
      }
    }
  }


  private def toPixelCoordinates(point2D: Point2D): Point2D = point2D match {
    case Point2D(x, y) => Point2D(x + currentLayer.canvas.width.value / 2, currentLayer.canvas.height.value / 2 - y)
  }

  private def toSpaceCoordinates(point2D: Point2D): Point2D = point2D match {
    case Point2D(x, y) => Point2D(x - currentLayer.canvas.width.value / 2, currentLayer.canvas.height.value / 2 - y)
  }


  def onAddLayer(): Unit = {
    val layerIndex = layers.lastOption.map(_.index + 1).getOrElse(0)
    val canvas = new Canvas()
    configureCanvas(canvas)
    layers += new Layer(canvas, None, layerIndex)
    setListItems()
    canvasStack.children.add(canvas)
  }

  private def removeLayer(index: Int): Unit = {
    if (index == currentLayerIndex) {
      cleanCanvas(layers(index).canvas)
    }

    layers.remove(index)
    visibleLayers.remove(index)
    canvasStack.children.remove(index+1)

    if (index == currentLayerIndex) {
      onLayerIndexChanged(index - 1)
    } else if (index < currentLayerIndex) {
      currentLayerIndex -= 1
    }

    setListItems()
  }

  def onRemoveLayer(): Unit = {
    if (currentLayerIndex >= 0) {
      removeLayer(currentLayerIndex)
    } else {
      // TODO: make button disabled?
    }
  }

  def toggleLayerVisibility(layerIndex: Int): Unit = {
    if (0 <= layerIndex && layerIndex < layers.size) {
      if (visibleLayers.contains(layerIndex)) {
        visibleLayers.remove(layerIndex)
      } else {
        visibleLayers.add(layerIndex)
      }
      val layer = layers(layerIndex)
      val isLayerVisible = visibleLayers.contains(layerIndex)
      layer.canvas.visible = isLayerVisible
      redrawLayers()
    } else {
      println("ERROR: index out of bounds")
    }
  }

}
