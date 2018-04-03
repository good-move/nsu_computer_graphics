package ru.nsu.fit.g15201.boltava.presentation_layer.workbench

import ru.nsu.fit.g15201.boltava.domain_layer.mesh.IsoLevel
import ru.nsu.fit.g15201.boltava.domain_layer.primitives._
import ru.nsu.fit.g15201.boltava.presentation_layer.AlertHelper
import ru.nsu.fit.g15201.boltava.presentation_layer.workbench.Contract.{IWorkbenchInteractor, IWorkbenchPresenter}
import scalafx.scene.canvas.Canvas
import scalafx.scene.input.MouseEvent
import scalafx.scene.layout.StackPane
import scalafx.scene.paint
import scalafx.stage.Stage
import scalafxml.core.macros.sfxml
import scalafx.Includes._

// TODO: Add color map
// TODO: Update isolines only when layer is visible

// Create `requireRedraw()` method???

@sfxml
class WorkbenchPresenter(wrapperPane: StackPane,
                         colorMapLayer: Canvas,
                         gridLayer: Canvas,
                         isolinesLayer: Canvas,
                         intersectionsLayer: Canvas,
                         interactor: IWorkbenchInteractor,
                         stage: Stage) extends IWorkbenchPresenter {

  private var isolineColor = paint.Color.Red

  {
    colorMapLayer.onMouseClicked = (event: MouseEvent) => onClick(event)
    intersectionsLayer.onMouseClicked = (event: MouseEvent) => onClick(event)
    isolinesLayer.onMouseClicked = (event: MouseEvent) => onClick(event)
    gridLayer.onMouseClicked = (event: MouseEvent) => onClick(event)

    interactor.setPresenter(this)
    makeAllLayersInvisible()
    bindLayersDimensions()
    createOnChangeHandlers()
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

  override def redrawColorMap(): Unit = {
    val gc = colorMapLayer.graphicsContext2D
    val writer = gc.pixelWriter
    val width = colorMapLayer.width.value
    val height = colorMapLayer.height.value

    for {
      x <- 0 until width.toInt
      y <- 0 until height.toInt
    } {
      val z = interactor.functionValue(x+0.5, y+0.5)
      val color = interactor.colorForValue(z)
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
    val isolineLevel = interactor.functionValue(Point2D(mouseEvent.x, mouseEvent.y))
    interactor.createIsoline(IsoLevel(isolineLevel))
  }

  override def setIsolineColor(color: Color): Unit = {
    val (alpha, red, green, blue) = ColorHelpers.colorFragments(color.color)
    isolineColor = paint.Color.color(red, green, blue, alpha)
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
  }

  def createOnChangeHandlers(): Unit = {
    wrapperPane.height.onChange { (_, _, height) =>
      if (height.doubleValue() > 0 && wrapperPane.width.value > 0) {
        val fieldDimensions = Dimensions(wrapperPane.width.value, height.doubleValue())
        interactor.handleWindowResize(fieldDimensions)
      }
    }

    wrapperPane.width.onChange { (_, _, width) =>
      if (width.doubleValue() > 0 && wrapperPane.height.value > 0) {
        val fieldDimensions = Dimensions(width.doubleValue(), wrapperPane.height.value)
        interactor.handleWindowResize(fieldDimensions)
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
