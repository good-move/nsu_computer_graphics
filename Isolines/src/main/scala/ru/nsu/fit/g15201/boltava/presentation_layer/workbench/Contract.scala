package ru.nsu.fit.g15201.boltava.presentation_layer.workbench

import ru.nsu.fit.g15201.boltava.domain_layer.mesh.IsoLevel
import ru.nsu.fit.g15201.boltava.domain_layer.primitives.{Color, Dimensions, Point2D, Segment}
import ru.nsu.fit.g15201.boltava.presentation_layer.IAlertInvoker
import ru.nsu.fit.g15201.boltava.presentation_layer.base.{IBaseInteractor, IBasePresenter}

object Contract {

  trait IWorkbenchPresenter extends IBasePresenter with IAlertInvoker {


    def setShowColorMap(visible: Boolean): Unit
    def setShowGrid(show: Boolean)
    def setShowIntersectionPoints(show: Boolean)
    def setShowIsolines(show: Boolean)

    def redrawGrid(xStep: Double, yStep: Double)
    def redrawIntersectionPoints(segments: Seq[Segment])
    def redrawIsolines(segments: Seq[Segment])
    def redrawColorMap(colorMapMode: ColorMapMode.Value)

    def setIsolineColor(color: Color)
    def setDimensions(dimensions: Dimensions)

  }

  trait IWorkbenchInteractor extends IBaseInteractor[IWorkbenchPresenter] {

    def interpolatedColorForValue(functionValue: Double): Color
    def colorForValue(functionValue: Double): Color

    def functionValue(point: Point2D): Double
    def functionValue(x: Double, y: Double): Double

    def createIsoline(level: IsoLevel)
    def handleWindowResize(fieldDimensions: Dimensions)

  }

  object ColorMapMode extends Enumeration {
    val Interpolated, Discrete = Value
  }

}
