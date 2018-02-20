package ru.nsu.fit.g15201.boltava.view

import javafx.application.Platform
import javafx.fxml.FXML
import javafx.scene.control.{Alert, ToolBar}
import javafx.scene.image.{ImageView, WritableImage}
import javafx.scene.input.MouseEvent
import javafx.scene.layout.HBox
import javafx.scene.paint.Color
import javafx.stage.Window

import ru.nsu.fit.g15201.boltava.model.canvas.{HexagonalGridController, IDrawable, IGridController, ImageDrawable}
import ru.nsu.fit.g15201.boltava.model.canvas.geometry.DoublePoint
import ru.nsu.fit.g15201.boltava.model.graphics.{BresenhamLineCreator, IColorFiller, ScanLineFiller}
import ru.nsu.fit.g15201.boltava.model.logic._

class MainViewController extends ICellStateObserver {

  private var drawable: IDrawable = _
  private var colorFiller: IColorFiller = _
  private var gridController: IGridController = _
  private var gameController: IGameLogicController = _

  private val ALIVE_CELL_COLOR = Color.GRAY
  private val DEAD_CELL_COLOR = Color.WHITE

  private val GRID_WIDTH =20
  private val GRID_HEIGHT = 20
  private val CELL_SIDE_SIZE = 20

  @FXML private var toolbar: ToolBar = _
  @FXML private var gameFieldImageView: ImageView = _
  @FXML private var gameFieldWrapper: HBox = _

  private var window: Window = _

  // ************************* Controller initialization *************************


  @FXML
  private def initialize(): Unit = {
    toolbar.getScene
    initializeGrid(GRID_WIDTH, GRID_HEIGHT)

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
    window = toolbar.getScene.getWindow
    if (!gameController.isGameModelSet) {
      AlertHelper.showError(
        window,
        "Game model not chosen",
        "Open a game model or create a new one by pressing corresponding toolbar buttons."
      )
      return
    }

    if (gameController.isGameFinished) {
      AlertHelper.showWarning(
        window,
        "Game has finished",
        "Choose alive cells to watch life happen."
      )
      return
    }

    // continue game in case it's already started
    if (gameController.isGameStarted) {
      gameController.start()
      return
    }

    drawCellGrid()
    gameController.start()
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

  private def drawCell(drawable: IDrawable, cell: Cell): Unit = {
    val vertices = cell.getVertices
    val verticesCount = vertices.length
    for (i <- vertices.indices) {
      val linePoints = BresenhamLineCreator.getLinePoints(vertices(i), vertices((i + 1) % verticesCount))
      drawable.draw(linePoints)
    }
  }

  private def drawCellGrid(): Unit = {
    val (width, height) = gridController.getCartesianFieldSize(GRID_WIDTH, GRID_HEIGHT)
    val image = new WritableImage(width.ceil.toInt, height.ceil.toInt)
    gameFieldImageView.setImage(image)
    drawable = new ImageDrawable(image)
    fillWhiteBackground(drawable)

    Platform.runLater(() => {
      gameController.getCells.foreach(_.foreach(cell => drawCell(drawable, cell)))
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
    val gridParameters = new GridParameters()
    gridParameters.cellSideSize = CELL_SIDE_SIZE
    gridParameters.width = width
    gridParameters.height = height
    gameController = new GameController(gridController=gridController)
//    gameController.setGridParams(gridParameters)
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

  override def onCellsStateChange(cells: Array[Array[Cell]]): Unit = {
    Platform.runLater(() => {
      println("a")
      cells.foreach(_.foreach(onCellStateChange))
      println("b")
    })
  }

}
