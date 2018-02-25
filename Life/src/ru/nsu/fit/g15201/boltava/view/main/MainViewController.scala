package ru.nsu.fit.g15201.boltava.view

import javafx.application.Platform
import javafx.fxml.{FXML, FXMLLoader}
import javafx.scene.{Node, Parent}
import javafx.scene.control.{Cell => _, _}
import javafx.scene.image.{Image, ImageView, WritableImage}
import javafx.scene.input.MouseEvent
import javafx.scene.layout._
import javafx.scene.paint.Color
import javafx.stage.FileChooser.ExtensionFilter
import javafx.stage.{FileChooser, Window}

import ru.nsu.fit.g15201.boltava.model.canvas._
import ru.nsu.fit.g15201.boltava.model.canvas.geometry.{DoublePoint, Point}
import ru.nsu.fit.g15201.boltava.model.graphics._
import ru.nsu.fit.g15201.boltava.model.logic.{ConfigManager, _}
import ru.nsu.fit.g15201.boltava.view.about.AboutDialog

import scala.collection.mutable.ArrayBuffer
import scala.reflect.io.Path

class MainViewController extends ICellStateObserver {

  private var drawable: IDrawable = _
  private val colorFiller: IColorFiller = new ScanLineFiller()
  private var gridController: IGridController = _
  private val gameController: IGameLogicController = new GameController()
  private val drawer: IDrawer = new BresenhamDrawer()

  private val ALIVE_CELL_COLOR = Color.GRAY
  private val DEAD_CELL_COLOR = Color.WHITE
  private val CELL_BORDER_COLOR = Color.BLACK

  @FXML private var root: VBox = _
  @FXML private var toolbar: ToolBar = _
  @FXML private var gameFieldImageView: ImageView = _
  @FXML private var scrollPane: ScrollPane = _
  @FXML private var setToggleModeBtn: ToggleButton = _
  @FXML private var setReplaceModeBtn: ToggleButton = _

  private var window: Window = _
  private var gridImage: WritableImage = _

  private var lastDraggedOverCell: Point = (-1, -1)

  // ************************* Controller initialization *************************

  @FXML
  private def initialize(): Unit = {
    gameController.subscribe(this)
    setEventHandlers()
  }

  private def setEventHandlers(): Unit = {
    gameFieldImageView.setPickOnBounds(true)
    gameFieldImageView.setOnMouseClicked((event: MouseEvent) => {
      onFieldClick((event.getX, event.getY))
      event.consume()
    })

    gameFieldImageView.setOnDragDetected((event: MouseEvent) => {
      gameFieldImageView.startFullDrag()
      event.consume()
    })

    gameFieldImageView.setOnMouseDragOver((event: MouseEvent) => {
      onFieldDragOver((event.getX, event.getY))
      event.consume()
    })

  }

  private def createNewGrid(configPath: String) = {
    try {
      val gridParameters = ConfigManager.openGameModel(configPath)
      gridController = new HexagonalGridController(gridParameters.cellSideSize)
      gameController.setGridController(gridController)
      gameController.setGridParams(gridParameters)
      drawGrid(gridParameters.width, gridParameters.height)
      fillAliveCells(gridParameters.aliveCells)
    } catch {
      case e: Exception =>
        println(e.getMessage)
        e.printStackTrace()
        AlertHelper.showError(window, "Failed to read configuration file", e.getMessage)
    }
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
    if (!gameController.isGameInitialized) {
      AlertHelper.showError(
        window,
        "Game model not chosen",
        "Open a game model or create a new one by pressing corresponding toolbar buttons."
      )
      return true
    }

    if (gameController.isGameFinished || gameController.isGameReset) {
      AlertHelper.showWarning(
        window,
        "Game is over",
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
    val fileChooser = createProperFileChooser("Open Game Model File")
    val file = fileChooser.showOpenDialog(toolbar.getScene.getWindow)
    if (file != null) {
      createNewGrid(file.getAbsolutePath)
    }
  }

  @FXML
  protected def onSaveModel(event: MouseEvent): Unit = {
    val fileChooser = createProperFileChooser("Select Model File")
    val file = fileChooser.showSaveDialog(toolbar.getScene.getWindow)

    if (file != null) {
      if (gameController.isGameRunning) {
        gameController.pause()
      }
      val aliveCells = new ArrayBuffer[Point]()
      gameController.getCells.foreach(_.foreach(cell => {
        if (cell.getState == State.ALIVE) {
          aliveCells.append((cell.getX, cell.getY))
        }
      }))
      ConfigManager.saveGameModel(file, gameController.getGridParams, aliveCells.toArray)
      if (gameController.isGamePaused) {
        gameController.start()
      }
    }
  }

  private def createProperFileChooser(title: String): FileChooser = {
    val fileChooser:FileChooser = new FileChooser()
    fileChooser.setTitle(title)
    fileChooser.getExtensionFilters.add(new ExtensionFilter(s"${ConfigManager.MODEL_FILE_DESCRIPTION}", s"*.${ConfigManager.MODEL_FILE_EXTENSION}"))
    fileChooser
  }

  @FXML
  def onSetReplace(event: MouseEvent): Unit = {
    if (!gameController.isGameInitialized) {
      showAlertOnError()
      setReplaceModeBtn.setSelected(false)
      return
    }
    gameController.setCellSelectionMode(CellSelectionMode.REPLACE)
    setToggleModeBtn.setSelected(false)
  }

  @FXML
  def onSetToggle(event: MouseEvent): Unit = {
    if (!gameController.isGameInitialized) {
      showAlertOnError()
      setToggleModeBtn.setSelected(false)
      return
    }
    gameController.setCellSelectionMode(CellSelectionMode.TOGGLE)
    setReplaceModeBtn.setSelected(false)
  }

  @FXML
  def onAbout(event: MouseEvent): Unit = {
    val owner = event.getSource.asInstanceOf[Node].getScene.getWindow
    val aboutDialog = new AboutDialog(owner)
    aboutDialog.show()
  }

  @FXML
  def onHelp(event: MouseEvent): Unit = {
    val owner = event.getSource.asInstanceOf[Node].getScene.getWindow
    val helpDialog = new HelpDialog(owner)
    helpDialog.show()
  }

  private def drawGrid(w: Int, h: Int): Unit = {
    val (width, height) = gridController.getCartesianFieldSize(w, h)
    gridImage = new WritableImage(width.ceil.toInt, height.ceil.toInt)
    gameFieldImageView.setImage(gridImage)
    drawable = new ImageDrawable(gridImage)

    Platform.runLater(() => {
      drawer.drawGrid(drawable, gameController.getCells, CELL_BORDER_COLOR)
    })
  }

  private def onFieldClick(point: DoublePoint): Unit = {
    val cellCoords = gridController.getCellByPoint(point)
    val cellGrid = gameController.getCells
    if (cellCoords.x < 0 || cellCoords.y < 0 ||
      cellCoords.x >= cellGrid.length || cellCoords.y >= cellGrid(0).length) return

    val cell = gameController.getCells(cellCoords.x)(cellCoords.y)
    gameController.onCellClicked(cell)
  }

  private def onFieldDragOver(point: DoublePoint): Unit = {
    val cellCoords = gridController.getCellByPoint(point)
    if (cellCoords.equals(lastDraggedOverCell)) {
      return
    }

    lastDraggedOverCell = cellCoords
    onFieldClick(point)
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

// todo: add menu
// todo: add ability to change grid parameters while playing
// todo: add About modal window
// todo: implement onExit method
