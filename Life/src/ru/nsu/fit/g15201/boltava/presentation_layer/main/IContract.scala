package ru.nsu.fit.g15201.boltava.presentation_layer.main

import javafx.scene.paint.Color

import ru.nsu.fit.g15201.boltava.domain_layer.canvas.geometry.DoublePoint
import ru.nsu.fit.g15201.boltava.domain_layer.logic.{Cell, ICellStateProvider}
import ru.nsu.fit.g15201.boltava.presentation_layer.base.{IBasePresenter, IBaseView}

object IContract {

  trait IPresenter extends IBasePresenter with ICellStateObserver with IGridStateObserver {
    def onFieldDragOver(point: DoublePoint)
    def onFieldClick(point: DoublePoint)
  }

  trait IView extends IBaseView[IPresenter] {

    def drawGrid(width: Int, height: Int, cells: Array[Array[Cell]], borderColor: Color)
    def fillCell(cell: Cell, color: Color)

  }

  trait IInteractor extends ICellStateProvider with IGridStateProvider {

    def onFieldClick(point: DoublePoint)

    def shouldUpdateDraggedCell(point: DoublePoint): Boolean

    def getGridPixelSize(width: Int, height: Int): (Int, Int)

  }

}
