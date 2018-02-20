package ru.nsu.fit.g15201.boltava.view

import javafx.application.Platform
import javafx.fxml.FXML
import javafx.scene.control.ToolBar
import javafx.scene.image.{ImageView, WritableImage}
import javafx.scene.input.MouseEvent
import javafx.scene.layout.HBox
import javafx.scene.paint.Color

import ru.nsu.fit.g15201.boltava.model.canvas.{HexagonalGridController, IDrawable, IGridController, ImageDrawable}
import ru.nsu.fit.g15201.boltava.model.canvas.geometry.DoublePoint
import ru.nsu.fit.g15201.boltava.model.graphics.{BresenhamLineCreator, IColorFiller, ScanLineFiller}
import ru.nsu.fit.g15201.boltava.model.logic.{Cell, GameController, IGameLogicController, State}

class MainViewController extends ICellStateObserver {

  private var drawable: IDrawable = _
  private var colorFiller: IColorFiller = _
  private var gridController: IGridController = _
  private var gameController: IGameLogicController = _

  private val ALIVE_CELL_COLOR = Color.GRAY
  private val DEAD_CELL_COLOR = Color.WHITE

  private val GRID_WIDTH = 8
  private val GRID_HEIGHT = 8
  private val CELL_SIDE_SIZE = 40

  @FXML private var toolbar: ToolBar = _
  @FXML private var gameFieldImageView: ImageView = _
  @FXML private var gameFieldWrapper: HBox = _

  // ************************* Controller initialization *************************


  @FXML
  private def initialize(): Unit = {
    initializeGrid(GRID_WIDTH, GRID_HEIGHT)

    val image = new WritableImage(1000, 1000)
    gameFieldImageView.setImage(image)
    drawable = new ImageDrawable(image)
    fillWhiteBackground(drawable)

    setEventHandlers()

    colorFiller = new ScanLineFiller()
  }

  private def setEventHandlers(): Unit = {
    gameFieldWrapper.setOnMouseClicked((event: MouseEvent) => {
      onFieldDragOrClick((event.getX, event.getY))
    })

    gameFieldWrapper.setOnDragDetected((event: MouseEvent) => {
      gameFieldImageView.startFullDrag()
      event.consume()
    })

    gameFieldWrapper.setOnMouseDragOver((event: MouseEvent) => {
      onFieldDragOrClick((event.getX, event.getY))
    })

  }

  // ************************* FXML events *************************

  @FXML
  protected def onPlay(event: MouseEvent): Unit = {
    println("onPlay")
    // if model not selected, alert <Model must be chosen>
    // else: draw grid and start game

    //    gameController.startGame()
    drawCellGrid()
  }

  @FXML
  protected def onPause(event: MouseEvent): Unit = {
    // if game started, pause game
    println("onPause")
  }

  @FXML
  protected def onReset(event: MouseEvent): Unit = {
    // stop game and clear field
    println("onClearField")
    onPause(event)
    gameController.reset()
  }

  @FXML
  protected def onNextStep(event: MouseEvent): Unit = {
    // pause game and make next step
    println("onNextStep")
    gameController.nextStep()
  }

  @FXML
  protected def onGameFieldClicked(event: MouseEvent): Unit = {
    println("hello")
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

  private def onFieldDragOrClick(point: DoublePoint): Unit = {
    val hexCoords = gridController.getCellByPoint(point)
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

  private def fillWhiteBackground(drawable: IDrawable): Unit = {
    for (x <- 0 until drawable.getWidth.toInt; y <- 0 until drawable.getHeight.toInt) {
      drawable.setColor((x, y), Color.WHITE)
    }
  }

  private def initializeGrid(width: Int, height: Int) = {
    gridController = new HexagonalGridController(CELL_SIDE_SIZE)
    gameController = new GameController(width, height, gridController)
    gameController.subscribe(this)
  }

  // ************************* ICellStateObserver *************************

  override def onCellStateChange(cell: Cell): Unit = {
    val color = cell.getState match {
      case State.ALIVE => ALIVE_CELL_COLOR
      case State.DEAD => DEAD_CELL_COLOR
    }

    fillCell(cell, color)
  }

  override def onCellsStateChange(cell: Array[Array[Cell]]): Unit = {
    Platform.runLater(() => {
      cell.foreach(_.foreach(onCellStateChange))
    })
  }

}
