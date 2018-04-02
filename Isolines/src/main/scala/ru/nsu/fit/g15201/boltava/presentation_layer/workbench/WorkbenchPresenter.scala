package ru.nsu.fit.g15201.boltava.presentation_layer.workbench

import ru.nsu.fit.g15201.boltava.domain_layer.primitives.{Color, ColorHelpers, Point2D, Segment}
import ru.nsu.fit.g15201.boltava.presentation_layer.workbench.Contract.{IWorkbenchInteractor, IWorkbenchPresenter}
import scalafx.scene.canvas.Canvas
import scalafx.scene.paint
import scalafx.stage.Stage
import scalafxml.core.macros.sfxml

// TODO: Add color map

@sfxml
class WorkbenchPresenter(gridLayer: Canvas,
                         intersectionsLayer: Canvas,
                         isolinesLayer: Canvas,
                         interactor: IWorkbenchInteractor,
                         stage: Stage) extends IWorkbenchPresenter {

  private var isolineColor = paint.Color.Black


  override def setShowGrid(visible: Boolean): Unit = {
    gridLayer.visible = visible
  }

  override def setShowIntersectionPoints(visible: Boolean): Unit = {
    intersectionsLayer.visible = visible
  }

  override def setShowIsolines(visible: Boolean): Unit = {
    isolinesLayer.visible = visible
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

    gc.fill = paint.Color.Black

    def drawPoint(point: Point2D): Unit = {
      gc.strokeOval(point.x, point.y, circleSize, circleSize)
    }

    for (segment <- segments) {
      drawPoint(segment.start)
      drawPoint(segment.end)
    }
  }

  override def redrawIsolines(segments: Seq[Segment]): Unit = {
    val gc = isolinesLayer.graphicsContext2D
    gc.lineWidth = 1
    gc.fill = isolineColor
    for (segment <- segments) {
      gc.strokeLine(segment.start.x, segment.start.y, segment.end.x, segment.end.y)
    }
  }

  override def onClick(): Unit = {
    // TODO: create new isoline
  }

  override def setIsolineColor(color: Color): Unit = {
    val (alpha, red, green, blue) = ColorHelpers.colorFragments(color.color)
    isolineColor = paint.Color.color(red, green, blue, alpha)
  }

}
