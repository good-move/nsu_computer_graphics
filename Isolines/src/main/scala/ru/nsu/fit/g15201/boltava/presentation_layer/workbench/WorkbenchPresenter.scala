package ru.nsu.fit.g15201.boltava.presentation_layer.workbench

import ru.nsu.fit.g15201.boltava.domain_layer.mesh.IsoLevel
import ru.nsu.fit.g15201.boltava.domain_layer.primitives._
import ru.nsu.fit.g15201.boltava.presentation_layer.AlertHelper
import ru.nsu.fit.g15201.boltava.presentation_layer.workbench.Contract.{ColorMapMode, IWorkbenchInteractor, IWorkbenchPresenter}
import scalafx.Includes._
import scalafx.scene.canvas.Canvas
import scalafx.scene.control.Label
import scalafx.scene.input.MouseEvent
import scalafx.scene.layout.StackPane
import scalafx.scene.paint
import scalafx.stage.Stage
import scalafxml.core.macros.sfxml

import scala.util.{Failure, Success, Try}


@sfxml
class WorkbenchPresenter(wrapperPane: StackPane,
                         colorMapLayer: Canvas,
                         gridLayer: Canvas,
                         isolinesLayer: Canvas,
                         intersectionsLayer: Canvas,
                         legendCanvas: Canvas,
                         statusBarLabel: Label,
                         interactor: IWorkbenchInteractor,
                         stage: Stage) extends IWorkbenchPresenter {

  private var isolineColor = paint.Color.Black

  {
    colorMapLayer.onMouseClicked = (event: MouseEvent) => onClick(event)
    intersectionsLayer.onMouseClicked = (event: MouseEvent) => onClick(event)
    isolinesLayer.onMouseClicked = (event: MouseEvent) => onClick(event)
    gridLayer.onMouseClicked = (event: MouseEvent) => onClick(event)

    interactor.setPresenter(this)
    makeAllLayersInvisible()
    bindLayersDimensions()
    createOnChangeHandlers()
    setStatusBarUpdater()

    val fieldDimensions = Dimensions(wrapperPane.width.value, wrapperPane.height.value)
    interactor.handleWindowResize(fieldDimensions)
    interactor.handleLegendResize(legendCanvas.width.value)

  }

  // ******************* Visibility controls *******************

  override def setShowGrid(visible: Boolean): Unit = {
    gridLayer.visible = visible
  }

  override def setShowIntersectionPoints(visible: Boolean): Unit = {
    intersectionsLayer.visible = visible
  }

  override def setShowIsolines(visible: Boolean): Unit = {
    isolinesLayer.visible = visible
  }

  override def setShowColorMap(visible: Boolean): Unit = {
    colorMapLayer.visible = visible
  }

  // ******************* Layers Redrawing *******************

  override def redrawColorMap(colorMapMode: ColorMapMode.Value): Unit = {
    redrawCanvas(
      colorMapLayer,
      colorMapMode,
      (x: Double, y: Double) => interactor.functionValue(x, y)
    )

  }

  override def redrawLegend(colorMapMode: ColorMapMode.Value): Unit = {
    redrawCanvas(
      legendCanvas,
      colorMapMode,
      (x: Double, y: Double) => interactor.legendFunctionValue(x, y)
    )
  }

  override def redrawLegendTicks(ticks: Seq[(Double, Double)]): Unit = {
    val tickWidth = 4d
    val height = legendCanvas.height.value
    val gc = legendCanvas.graphicsContext2D
    val writer = gc.pixelWriter

    gc.fill = paint.Color.Black

    for ((tickPosition, tickValue) <- ticks) {
      val x = tickPosition - tickWidth/2
      val y = height / 2
      gc.fillRect(x, 0, tickWidth, y)
      gc.fillText(f"$tickValue%.4f", x, 0.8* height)
    }
  }

  private def redrawCanvas(canvas: Canvas, colorMapMode: ColorMapMode.Value, functionValue: (Double, Double) => Double): Unit = {
    val gc = canvas.graphicsContext2D
    val writer = gc.pixelWriter
    val width = canvas.width.value
    val height = canvas.height.value

    val colorForValue = colorMapMode match {
      case ColorMapMode.Discrete => (z: Double) => interactor.colorForValue(z)
      case ColorMapMode.Interpolated => (z: Double) => interactor.interpolatedColorForValue(z)
    }

    for {
      x <- 0 until width.toInt
      y <- 0 until height.toInt
    } {
      val z = functionValue(x+0.5, y+0.5)
      val color = colorForValue(z)
      writer.setArgb(x, y, color.color)
    }
  }

  override def redrawGrid(xStep: Double, yStep: Double): Unit = {
    val gc = gridLayer.graphicsContext2D
    val width = gridLayer.width.value
    val height = gridLayer.height.value

    gc.clearRect(0, 0, width, height)

    for (x <- 0d to width by xStep) {
      gc.strokeLine(x, 0, x, height)
    }

    for (y <- 0d to height by yStep) {
      gc.strokeLine(0, y, width, y)
    }
  }

  override def redrawIntersectionPoints(segments: Seq[Segment]): Unit = {
    val gc = intersectionsLayer.graphicsContext2D
    val circleSize = 4

    val width = intersectionsLayer.width.value
    val height = intersectionsLayer.height.value

    gc.clearRect(0, 0, width, height)

    gc.fill = paint.Color.DarkBlue
    gc.stroke = paint.Color.Grey

    def drawPoint(point: Point2D): Unit = {
      gc.strokeOval(point.x, point.y, circleSize, circleSize)
      gc.fillOval(point.x, point.y, circleSize, circleSize)
    }

    for (segment <- segments) {
      drawPoint(segment.start)
      drawPoint(segment.end)
    }
  }

  override def redrawIsolines(segments: Seq[Segment]): Unit = {
    val gc = isolinesLayer.graphicsContext2D

    val width = isolinesLayer.width.value
    val height = isolinesLayer.height.value

    gc.clearRect(0, 0, width, height)

    gc.lineWidth = 1
    gc.stroke = isolineColor

    for (segment <- segments) {
      gc.strokeLine(segment.start.x, segment.start.y, segment.end.x, segment.end.y)
    }
  }

  def onClick(mouseEvent: MouseEvent): Unit = {
    Try(interactor.functionValue(Point2D(mouseEvent.x, mouseEvent.y))) match {
      case Success(isolineLevel) =>
        interactor.createIsoline(IsoLevel(isolineLevel))
      case Failure(throwable) =>
        AlertHelper.showError(stage, "Cannot compute function value", throwable.getMessage)
    }

  }

  override def setIsolineColor(color: Color): Unit = {
    val (alpha, red, green, blue) = ColorHelpers.colorFragments(color.color)
    isolineColor = paint.Color.color(red, green, blue, alpha)
  }


  // ************************ Handlers Binding ************************

  def setStatusBarUpdater(): Unit = {
    wrapperPane.onMouseMoved =  (mouseEvent: MouseEvent) => {
      val statusBarText = interactor.domainPoint(mouseEvent.x, mouseEvent.y) match {
        case Some((x, y)) =>
          Try(interactor.functionValue(mouseEvent.x, mouseEvent.y)) match {
            case Success(z) =>
              f"x: $x%.3f   y: $y%.3f   z: $z%.3f"
            case Failure(throwable) =>
              AlertHelper.showError(stage, "Cannot compute function value", throwable.getMessage)
              "Model not initialized"
          }
        case None => "Model not initialized"
      }
      statusBarLabel.text = statusBarText
    }
  }

  private def bindToWrapperDimensions(canvas: Canvas): Unit = {
    canvas.width <== wrapperPane.width
    canvas.height <== wrapperPane.height
  }

  private def bindLayersDimensions(): Unit = {
    bindToWrapperDimensions(gridLayer)
    bindToWrapperDimensions(intersectionsLayer)
    bindToWrapperDimensions(isolinesLayer)
    bindToWrapperDimensions(colorMapLayer)

    legendCanvas.width <== wrapperPane.width
  }

  def createOnChangeHandlers(): Unit = {
    wrapperPane.height.onChange { (_, _, height) =>
      if (height.doubleValue() > 0 && wrapperPane.width.value > 0) {
        val fieldDimensions = Dimensions(wrapperPane.width.value, height.doubleValue())
        interactor.handleWindowResize(fieldDimensions)
        interactor.handleLegendResize(legendCanvas.width.value)
      }
    }

    wrapperPane.width.onChange { (_, _, width) =>
      if (width.doubleValue() > 0 && wrapperPane.height.value > 0) {
        val fieldDimensions = Dimensions(width.doubleValue(), wrapperPane.height.value)
        interactor.handleWindowResize(fieldDimensions)
        interactor.handleLegendResize(legendCanvas.width.value)
      }
    }
  }

  override def setDimensions(dimensions: Dimensions): Unit = {
    wrapperPane.prefWidth = dimensions.width
    wrapperPane.prefHeight = dimensions.height
  }

  def makeAllLayersInvisible(): Unit = {
    gridLayer.visible = false
    isolinesLayer.visible = false
    intersectionsLayer.visible = false
    colorMapLayer.visible = false
  }

  override def showError(title: String, message: String): Unit = {
    AlertHelper.showError(stage, title, message)
  }

  override def showWarning(title: String, message: String): Unit = {
    AlertHelper.showWarning(stage, title, message)
  }

  override def showInformation(title: String, message: String): Unit = {
    AlertHelper.showInformation(stage, title, message)
  }

  override def showConfirmation(title: String, message: String): Unit = {
    AlertHelper.showConfirmation(stage, title, message)
  }

}
