package ru.nsu.fit.g15201.boltava.presentation_layer.main

import javafx.application.Platform
import javafx.fxml.FXML
import javafx.scene.image.{ImageView, WritableImage}
import javafx.scene.input.MouseEvent
import javafx.scene.paint.Color

import ru.nsu.fit.g15201.boltava.domain_layer.canvas._
import ru.nsu.fit.g15201.boltava.domain_layer.graphics._
import ru.nsu.fit.g15201.boltava.domain_layer.logic._
import ru.nsu.fit.g15201.boltava.presentation_layer.main.IContract.{IPresenter, IView}



class PlaygroundView extends IView {

  private var drawable: IDrawable = _
  private val colorFiller: IColorFiller = new ScanLineFiller()
  private val drawer: IDrawer = new BresenhamDrawer()

  private var presenter: IPresenter = _

  private var isDragging = false

  @FXML var gameFieldImageView: ImageView = _

  private var gridImage: WritableImage = _

  @FXML
  def initialize(): Unit = {
    PlaygroundView.instance = this
    setEventHandlers()
  }

  private def setEventHandlers(): Unit = {
    gameFieldImageView.setPickOnBounds(true)

    gameFieldImageView.setOnMouseClicked((event: MouseEvent) => {
      if (!isDragging) {
        presenter.onFieldClick((event.getX, event.getY))
      } else {
        isDragging = false
      }
      event.consume()
    })

    gameFieldImageView.setOnDragDetected((event: MouseEvent) => {
      gameFieldImageView.startFullDrag()
      isDragging = true
      event.consume()
    })

    gameFieldImageView.setOnMouseDragOver((event: MouseEvent) => {
      presenter.onFieldDragOver((event.getX, event.getY))
      event.consume()
    })

  }

  override def drawGrid(width: Int, height: Int, cells: Array[Array[Cell]], borderColor: Color): Unit = {
    gridImage = new WritableImage(width, height)
    gameFieldImageView.setImage(gridImage)
    drawable = new ImageDrawable(gridImage)

    fillBackground()

    Platform.runLater(() => {
      drawer.drawGrid(drawable, cells, borderColor)
    })
  }

  override def fillCell(cell: Cell, color: Color): Unit = {
    colorFiller.fillCell(drawable, cell, color)
  }

  override def setPresenter(presenter: IPresenter): Unit = this.presenter = presenter

  private def fillBackground() {
    for (x <- 0 until gridImage.getWidth.toInt; y <- 0 until gridImage.getHeight.toInt) {
      drawable.setColor((x, y), Color.WHITE)
    }
  }

}


object PlaygroundView {
  private var instance: PlaygroundView = _

  def getInstance: PlaygroundView = instance
}