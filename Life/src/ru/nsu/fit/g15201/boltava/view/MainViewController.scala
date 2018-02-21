package ru.nsu.fit.g15201.boltava.view

import javafx.application.Platform
import javafx.fxml.FXML
import javafx.scene.control.{ScrollPane, ToolBar}
import javafx.scene.image.{ImageView, WritableImage}
import javafx.scene.input.MouseEvent
import javafx.scene.layout._
import javafx.scene.paint.Color
import javafx.stage.{FileChooser, Window}

import ru.nsu.fit.g15201.boltava.model.canvas.{HexagonalGridController, IDrawable, IGridController, ImageDrawable}
import ru.nsu.fit.g15201.boltava.model.canvas.geometry.DoublePoint
import ru.nsu.fit.g15201.boltava.model.graphics.{BresenhamLineCreator, IColorFiller, ScanLineFiller}
import ru.nsu.fit.g15201.boltava.model.logic._

class MainViewController extends ICellStateObserver {

  private val DEFAULT_CONFIG_PATH = "./config.txt"

  private var drawable: IDrawable = _
  private var colorFiller: IColorFiller = _
  private var gridController: IGridController = _
  private val gameController: IGameLogicController = new GameController

  private val ALIVE_CELL_COLOR = Color.GRAY
  private val DEAD_CELL_COLOR = Color.WHITE

  private val MAX_GRID_SIDE_SIZE = 500
  private val MAX_BORDER_WIDTH = 15
  private val MAX_CELL_SIDE_SIZE = 50

  @FXML private var root: VBox = _
  @FXML private var toolbar: ToolBar = _
  @FXML private var gameFieldImageView: ImageView = _
  @FXML private var scrollPane: ScrollPane = _

  private var window: Window = _
  private var gridImage: WritableImage = _

  // ************************* Controller initialization *************************

  @FXML
  private def initialize(): Unit = {
//    createNewGrid(DEFAULT_CONFIG_PATH)
    setEventHandlers()
    colorFiller = new ScanLineFiller()
    VBox.setVgrow(scrollPane, Priority.ALWAYS)
  }

  private def setEventHandlers(): Unit = {
    gameFieldImageView.setOnMouseClicked((event: MouseEvent) => {
//      println("hello")
      onFieldDragOrClick((event.getX, event.getY))
      event.consume()
    })

    gameFieldImageView.setOnDragDetected((event: MouseEvent) => {
      gameFieldImageView.startFullDrag()
      event.consume()
    })

    gameFieldImageView.setOnMouseDragOver((event: MouseEvent) => {
      onFieldDragOrClick((event.getX, event.getY))
      event.consume()
    })

  }

  private def createNewGrid(configPath: String) = {
    try {
      val gridParameters = ConfigReader.parseConfig(configPath)
      validateGridParameters(gridParameters)
      gameController.unsubscribe(this)
      gridController = new HexagonalGridController(gridParameters.cellSideSize)
      gameController.setGridParams(gridParameters)
      gameController.subscribe(this)
      drawCellGrid(gridParameters.width, gridParameters.height)
      fillAliveCells(gridParameters.aliveCells)
    } catch {
      case e: Exception =>
        println(e.getMessage)
        AlertHelper.showError(window, "Failed to read configuration file", e.getMessage)
    }
  }

  private def validateGridParameters(gridParameters: GridParameters) = {
    if (gridParameters.width <= 0 || gridParameters.width > MAX_GRID_SIDE_SIZE ||
        gridParameters.height <= 0 || gridParameters.height > MAX_GRID_SIDE_SIZE) {
      throw new RuntimeException(
        s"Invalid grid dimensions. Grid width and height " +
          s"must be positive integers between 1 and $MAX_GRID_SIDE_SIZE.")
    }

    if (gridParameters.borderWidth <= 0 || gridParameters.borderWidth > MAX_BORDER_WIDTH) {
      throw new RuntimeException(s"Border width must be a positive integer not greater than $MAX_BORDER_WIDTH.")
    }

    if (gridParameters.cellSideSize <= 0 || gridParameters.cellSideSize > MAX_CELL_SIDE_SIZE) {
      throw new RuntimeException(s"Cell side size must be a positive integer not grater than $MAX_CELL_SIDE_SIZE.")
    }

    gridParameters.aliveCells.foreach(cell => {
      if (cell._1 < 0 || cell._1 >= gridParameters.width ||
          cell._2 < 0 || cell._2 >= gridParameters.height) {
        throw new RuntimeException(s"Cell coordinates out of bounds: $cell " +
          s"(width: ${gridParameters.width}, height: ${gridParameters.height}).")
      }
    })

  }

  private def fillAliveCells(aliveCells: Array[(Int, Int)]): Unit = {
    Platform.runLater(() => {
      aliveCells
        .map(coords => gameController.getCells(coords._1)(coords._2))
        .foreach(cell => gameController.onCellClicked(cell))
    })
  }

  // ************************* FXML events *************************

  private def showAlertOnError(): Boolean = {
    if (!gameController.isGameModelSet) {
      AlertHelper.showError(
        window,
        "Game model not chosen",
        "Open a game model or create a new one by pressing corresponding toolbar buttons."
      )
      return true
    }

    if (gameController.isGameFinished) {
      AlertHelper.showWarning(
        window,
        "Game has finished",
        "Choose alive cells to watch life happen."
      )
      return true
    }

    false
  }

  @FXML
  protected def onPlay(event: MouseEvent): Unit = {
    println("onPlay")
    window = toolbar.getScene.getWindow

    val alertShown = showAlertOnError()
    if (alertShown) {
      return
    }

    // continue game in case it's already started
    if (gameController.isGameStarted) {
      gameController.start()
      return
    }

    gameController.start()
  }

  @FXML
  protected def onPause(event: MouseEvent): Unit = {
    val alertShown = showAlertOnError()
    if (alertShown) {
      return
    }

    // if game started, pause game
    println("onPause")
    gameController.pause()
  }

  @FXML
  protected def onReset(event: MouseEvent): Unit = {
    val alertShown = showAlertOnError()
    if (alertShown) {
      return
    }

    // stop game and clear field
    println("onClearField")
    onPause(event)
    gameController.reset()
  }

  @FXML
  protected def onNextStep(event: MouseEvent): Unit = {
    val alertShown = showAlertOnError()
    if (alertShown) {
      return
    }

    // pause game and make next step
    println("onNextStep")
    gameController.nextStep()
  }

  @FXML
  protected def onOpenModel(event: MouseEvent): Unit = {
    val fileChooser:FileChooser = new FileChooser()
    fileChooser.setTitle("Open Resource File")
    val file = fileChooser.showOpenDialog(toolbar.getScene.getWindow)
    if (file != null) {
      createNewGrid(file.getAbsolutePath)
    }
  }

  private def drawCell(drawable: IDrawable, cell: Cell): Unit = {
    val vertices = cell.getVertices
    val verticesCount = vertices.length
    for (i <- vertices.indices) {
      val linePoints = BresenhamLineCreator.getLinePoints(vertices(i), vertices((i + 1) % verticesCount))
      drawable.draw(linePoints)
    }
  }

  private def drawCellGrid(w: Int, h: Int): Unit = {
    val (width, height) = gridController.getCartesianFieldSize(w, h)
    gridImage = new WritableImage(width.ceil.toInt, height.ceil.toInt)
    gameFieldImageView.setImage(gridImage)
    drawable = new ImageDrawable(gridImage)

    Platform.runLater(() => {
      gameController.getCells.foreach(_.foreach(cell => drawCell(drawable, cell)))
    })
  }

  private def onFieldDragOrClick(point: DoublePoint): Unit = {
    val cellCoords = gridController.getCellByPoint(point)
    val cellGrid = gameController.getCells
    println(s"$point -> $cellCoords")
    if (cellCoords.x < 0 || cellCoords.y < 0 ||
      cellCoords.x >= cellGrid.length || cellCoords.y >= cellGrid(0).length) return

    val cell = gameController.getCells(cellCoords.x)(cellCoords.y)
    gameController.onCellClicked(cell)
  }

  private def fillCell(cell: Cell, color: Color): Unit = {
    colorFiller.fillCell(drawable, cell, color)
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
      cells.foreach(_.foreach(onCellStateChange))
    })
  }

}
