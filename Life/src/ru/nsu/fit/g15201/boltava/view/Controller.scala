package ru.nsu.fit.g15201.boltava.view

import javafx.application.Platform
import javafx.fxml.FXML
import javafx.scene.control.ToolBar
import javafx.scene.image.{ImageView, WritableImage}
import javafx.scene.input.MouseEvent
import javafx.scene.layout.HBox
import javafx.scene.paint.Color

import ru.nsu.fit.g15201.boltava.model.canvas.{HexagonalGridController, IDrawable, IGridController, ImageDrawable}
import ru.nsu.fit.g15201.boltava.model.canvas.geometry.{DoublePoint, Point}
import ru.nsu.fit.g15201.boltava.model.graphics.{BresenhamLineCreator, IColorFiller, ScanLineFiller}
import ru.nsu.fit.g15201.boltava.model.logic.{Cell, GameController, State}

class Controller extends ICellStateObserver {

  private var drawable: IDrawable = _
  private var colorFiller: IColorFiller = _
  private var gameController: GameController = _

  private val aliveCellFillColor = Color.GRAY
  private val deadCellFillColor = Color.WHITE

  private val gridWidth = 8
  private val gridHeight = 8
  private val cellSideSize = 40

  @FXML private var toolbar: ToolBar = _
  @FXML private var gameFieldImageView: ImageView = _
  @FXML private var gameFieldWrapper: HBox = _

  var initialized = false

  private def initialize(): Unit = {
    if (initialized) return

    initHexagonGrid(gridWidth, gridHeight)

    val image = new WritableImage(1000, 1000)
    for (x <- 0 until image.getWidth.toInt; y <- 0 until image.getHeight.toInt) image.getPixelWriter.setColor(x, y, Color.WHITE)
    gameFieldImageView.setImage(image)
    drawable = new ImageDrawable(image)

    setEventHandlers()

    colorFiller = new ScanLineFiller()

    initialized = true
  }

  private def initHexagonGrid(width: Int, height: Int) = {
    val gridController: IGridController = new HexagonalGridController(cellSideSize)
    gameController = new GameController(width, height, gridController)
    gameController.subscribe(this)
  }

  private def drawLine(drawable: IDrawable, points: Array[Point]): Unit = {
    for (p <- points) {
      drawable.setColor(p, Color.BLACK)
    }
  }

  private def drawHex(drawable: IDrawable, hex: Cell): Unit = {
    val vertices = hex.getVertices
    val verticesCount = vertices.length
    for (i <- vertices.indices) {
      val linePoints = BresenhamLineCreator.getLinePoints(vertices(i), vertices((i + 1) % verticesCount))
      drawable.draw(linePoints)
    }
  }

  private def drawCellGrid(): Unit = {
    Platform.runLater(() => {
      gameController.getCells.foreach(_.foreach(cell => drawHex(drawable, cell)))
    })
  }

  private def setEventHandlers(): Unit = {
    gameFieldWrapper.setOnMouseClicked((event: MouseEvent) => {
      onFieldDragOrClick((event.getX, event.getY))
    })

    gameFieldWrapper.addEventHandler(MouseEvent.DRAG_DETECTED, (event: MouseEvent) => {
      gameFieldImageView.startFullDrag()
      event.consume()
    })

    gameFieldWrapper.setOnMouseDragOver((event: MouseEvent) => {
      onFieldDragOrClick((event.getX, event.getY))
    })

  }

  private def onFieldDragOrClick(point: DoublePoint): Unit = {
    val hexCoords = gameController.getGridController.getCellByPoint(point)
    val cellGrid = gameController.getCells
    println(s"$point -> $hexCoords")
    if (hexCoords.x < 0 || hexCoords.y < 0 ||
      hexCoords.x >= cellGrid.length || hexCoords.y >= cellGrid(0).length) return

    val hexagon = gameController.getCells(hexCoords.x)(hexCoords.y)
    gameController.onCellClicked(hexagon)
  }

  private def fillCell(cell: Cell, color: Color): Unit = {
        colorFiller.fillCell(drawable, cell, color)
  }

  override def onCellStateChange(cell: Cell): Unit = {
    val color = cell.getState match {
      case State.ALIVE => aliveCellFillColor
      case State.DEAD => deadCellFillColor
    }

    fillCell(cell, color)
  }

  override def onCellsStateChange(cell: Array[Array[Cell]]): Unit = {
    Platform.runLater(() => {
      cell.foreach(_.foreach(onCellStateChange))
    })
  }

  @FXML
  protected def onPlay(event: MouseEvent): Unit = {
    initialize()
    println("onPlay")
//    gameController.startGame()
    drawCellGrid()
  }

  @FXML
  protected def onPause(event: MouseEvent): Unit = {
    println("onPause")
  }

  @FXML
  protected def onClearField(event: MouseEvent): Unit = {
    println("onClearField")
    onPause(event)
    gameController.clearCellsField()
  }

  @FXML
  protected def onNextStep(event: MouseEvent): Unit = {
    println("onNextStep")
    gameController.nextStep()
  }

  @FXML
  protected def onGameFieldClicked(event: MouseEvent): Unit = {
    println("hello")
  }

}
