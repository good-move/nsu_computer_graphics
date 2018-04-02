package ru.nsu.fit.g15201.boltava.presentation_layer.workbench

import ru.nsu.fit.g15201.boltava.domain_layer.mesh.IsoLevel
import ru.nsu.fit.g15201.boltava.domain_layer.primitives.{Color, Point2D, Segment}
import ru.nsu.fit.g15201.boltava.presentation_layer.base.{IBaseInteractor, IBasePresenter}

object Contract {

  trait IWorkbenchPresenter extends IBasePresenter {

    def setShowGrid(show: Boolean)
    def setShowIntersectionPoints(show: Boolean)
    def setShowIsolines(show: Boolean)

    def redrawGrid(xStep: Double, yStep: Double)
    def redrawIntersectionPoints(segments: Seq[Segment])
    def redrawIsolines(segments: Seq[Segment])

    def onClick()

    def setIsolineColor(color: Color)

  }

  trait IWorkbenchInteractor extends IBaseInteractor {

    def functionValue(point: Point2D)
    def createIsoline(level: IsoLevel)

  }

}
