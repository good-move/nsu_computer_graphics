package ru.nsu.fit.g15201.boltava.presentation_layer.main

import javafx.application.Platform
import javafx.scene.paint.Color

import ru.nsu.fit.g15201.boltava.domain_layer.canvas.geometry.DoublePoint
import ru.nsu.fit.g15201.boltava.domain_layer.logic.{Cell, State}
import ru.nsu.fit.g15201.boltava.presentation_layer.main.IContract.{IInteractor, IPresenter, IView}

class PlaygroundPresenter(view: IView, interactor: IInteractor) extends IPresenter {

  private val ALIVE_CELL_COLOR = Color.ORANGE
  private val DEAD_CELL_COLOR = Color.WHITE
  private val CELL_BORDER_COLOR = Color.BLACK
  private val IMPACT_SCORE_COLOR = Color.BLUE

  private var isShowImpactEnabled = false

  {
    view.setPresenter(this)
    interactor.addCellStateObserver(this)
    interactor.addGridStateObserver(this)
  }

  override def onFieldDragOver(point: DoublePoint): Unit = {
    if (interactor.shouldUpdateDraggedCell(point)) {
      onFieldClick(point)
    }
  }

  override def onFieldClick(point: DoublePoint): Unit = {
    interactor.onFieldClick(point)
  }

  override def onCellStateChange(cell: Cell): Unit = {
    val color = cell.getState match {
      case State.ALIVE => ALIVE_CELL_COLOR
      case State.DEAD => DEAD_CELL_COLOR
    }

    view.fillCell(cell, color)
    if (isShowImpactEnabled) {
      view.drawCellImpact(cell, IMPACT_SCORE_COLOR)
    }
  }

  override def onCellsStateChange(cells: Array[Array[Cell]]): Unit = {
    Platform.runLater(() => {
      cells.foreach(_.foreach(onCellStateChange))
    })
  }

  override def onGridStructureChange(grid: Array[Array[Cell]], aliveCells: Array[Cell]): Unit = {
    val (width, height) = interactor.getGridPixelSize(grid(0).length, grid.length)
    view.drawGrid(width, height, grid, CELL_BORDER_COLOR)
    Platform.runLater(() => {
      aliveCells.foreach(onCellStateChange)
    })
  }

  override def onShowImpactChange(isShowImpactEnabled: Boolean, cells: Option[Array[Array[Cell]]]): Unit = {
    this.isShowImpactEnabled = isShowImpactEnabled
    if (isShowImpactEnabled) {
      Platform.runLater(() => {
        cells.get.foreach(_.foreach(cell => view.drawCellImpact(cell, IMPACT_SCORE_COLOR)))
      })
    } else {
      view.clearImpactScores()
    }
  }

}
