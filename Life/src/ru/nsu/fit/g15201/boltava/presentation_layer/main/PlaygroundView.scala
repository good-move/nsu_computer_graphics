package ru.nsu.fit.g15201.boltava.presentation_layer.main

import javafx.application.Platform
import javafx.fxml.FXML
import javafx.scene.canvas.Canvas
import javafx.scene.effect.BlendMode
import javafx.scene.image.{ImageView, WritableImage}
import javafx.scene.input.MouseEvent
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import javafx.scene.text.Text

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

  var gameFieldImageView: ImageView = _
  var impactScoresCanvas: Canvas = _
  @FXML var stackPane: StackPane = _

  @FXML
  def initialize(): Unit = {
    PlaygroundView.instance = this
    setEventHandlers()

  }

  private def setEventHandlers(): Unit = {
    gameFieldImageView = new ImageView()

    stackPane.setPickOnBounds(true)

    stackPane.setOnMouseClicked((event: MouseEvent) => {
      if (!isDragging) {
        presenter.onFieldClick((event.getX, event.getY))
      } else {
        isDragging = false
      }
      event.consume()
    })

    stackPane.setOnDragDetected((event: MouseEvent) => {
      stackPane.startFullDrag()
      isDragging = true
      event.consume()
    })

    stackPane.setOnMouseDragOver((event: MouseEvent) => {
      presenter.onFieldDragOver((event.getX, event.getY))
      event.consume()
    })

  }

  override def drawGrid(width: Int, height: Int, cells: Array[Array[Cell]], borderColor: Color): Unit = {
    stackPane.getChildren.removeAll(gameFieldImageView, impactScoresCanvas)

    val gridImage = new WritableImage(width, height)
    impactScoresCanvas = new Canvas(width, height)
    gameFieldImageView.setImage(gridImage)
    drawable = new ImageDrawable(gridImage)

    fillBackground()

    Platform.runLater(() => {
      drawer.drawGrid(drawable, cells, borderColor)
    })

    impactScoresCanvas.setBlendMode(BlendMode.SRC_OVER)
    stackPane.getChildren.addAll(
      gameFieldImageView,
      impactScoresCanvas
    )
  }

  override def fillCell(cell: Cell, color: Color): Unit = {
    colorFiller.fillCell(drawable, cell, color)
  }

  override def drawCellImpact(cell: Cell, color: Color): Unit = {
    val text = new Text(cell.getCenter.x, cell.getCenter.y, f"${cell.getImpact}%1.1f")
    val textWidth = text.getLayoutBounds.getWidth
    val textHeight = text.getLayoutBounds.getHeight
    val x = cell.getCenter.x - textWidth/2.5
    val y = cell.getCenter.y.toDouble + textHeight/4

    impactScoresCanvas.getGraphicsContext2D.clearRect(x-textWidth/4, y-textHeight/1.5, 1.5*textWidth, 1.2*textHeight)
    impactScoresCanvas.getGraphicsContext2D.strokeText(text.getText, x, y)
  }

  override def setPresenter(presenter: IPresenter): Unit = this.presenter = presenter

  private def fillBackground() {
    for {x <- 0 until gameFieldImageView.getImage.getWidth.toInt
         y <- 0 until gameFieldImageView.getImage.getHeight.toInt} {
      drawable.setColor((x, y), Color.WHITE)
    }
  }

}


object PlaygroundView {
  private var instance: PlaygroundView = _

  def getInstance: PlaygroundView = instance
}