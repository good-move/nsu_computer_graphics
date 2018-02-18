package ru.nsu.fit.g15201.boltava.view

import javafx.application.Application
import javafx.scene.image.{ImageView, WritableImage}
import javafx.scene.input.MouseEvent
import javafx.scene.paint.Color
import javafx.scene.{Group, Scene}
import javafx.stage.Stage

import ru.nsu.fit.g15201.boltava.model.canvas._
import ru.nsu.fit.g15201.boltava.model.canvas.geometry.{DoublePoint, Point, Polygon}
import ru.nsu.fit.g15201.boltava.model.graphics.{BresenhamLineCreator, IColorFiller, ScanLineFiller}
import ru.nsu.fit.g15201.boltava.model.logic.{Cell, GameController, HexagonCell}

class LifeGame extends Application with ICellStateObserver {

  private var drawable: IDrawable = _
  private var colorFiller: IColorFiller = _
  private var gameController: GameController[HexagonCell] = _

  private val aliveCellFillColor = Color.GRAY
  private val deadCellFillColor = Color.WHITE

  private var scene: Scene = _

  override def start(primaryStage: Stage): Unit = {
    // TODO:
    // read config file
    // create and resize field
    // init stage and UI controls
    // create hexagons and mark them as alive or dead
    // draw hexagons

    val gridWidth = 10
    val gridHeight = 7
    initStage(primaryStage)
    initHexagonGrid(gridWidth, gridHeight)
    drawHexagonGrid()

    setEventHandlers()

    showStage(primaryStage)
  }

  def drawLine(drawable: IDrawable, points: Array[Point]): Unit = {
    for (p <- points) {
      drawable.setColor(p, Color.BLACK)
    }
  }

  def drawHex(drawable: IDrawable, hex: HexagonCell): Unit = {
    val vertices = hex.getVertices
    val verticesCount = vertices.length
    for (i <- vertices.indices) {
      val linePoints = BresenhamLineCreator.getLinePoints(vertices(i), vertices((i + 1) % verticesCount))
      drawable.draw(linePoints)
    }
  }

  private def initStage(primaryStage: Stage) = {
    val root = new Group()
    val image = new WritableImage(1100, 600)
    val view = new ImageView(image)
    root.getChildren.addAll(view)

    scene = new Scene(root)
    primaryStage.setScene(scene)

    drawable = new ImageDrawable(image)
    colorFiller = new ScanLineFiller()
  }

  private def showStage(primaryStage: Stage): Unit = {
    primaryStage.show()
  }

  private def initHexagonGrid(width: Int, height: Int) = {
    gameController = new GameController[HexagonCell](width, height, new HexagonalGridController(50))
    gameController.subscribe(this)
  }

  private def drawHexagonGrid(): Unit = {
    gameController.getCells.foreach(_.foreach(hexagon => drawHex(drawable, hexagon)))
  }

  private def setEventHandlers(): Unit = {
    scene.addEventHandler(MouseEvent.MOUSE_CLICKED, (event: MouseEvent) => {
      onFieldDragOrClick((event.getSceneX, event.getSceneY))
      event.consume()
    })

    scene.addEventHandler(MouseEvent.DRAG_DETECTED, (event: MouseEvent)=> {
      scene.startFullDrag()
      event.consume()
    })

    scene.setOnMouseDragOver((event: MouseEvent) => {
      onFieldDragOrClick((event.getSceneX, event.getSceneY))
    })

  }

  private def onFieldDragOrClick(point: DoublePoint): Unit = {
    val hexCoords = gameController.getGridController.getCellByPoint(point)
    val cellGrid = gameController.getCells
    if (hexCoords.x < 0 || hexCoords.y < 0 ||
      hexCoords.x >= cellGrid.length || hexCoords.y >= cellGrid(0).length) return

    val hexagon = gameController.getCells(hexCoords.x)(hexCoords.y)
    gameController.onCellClicked(hexagon)
  }

  private def fillCell(hexagonCell: Polygon, color: Color): Unit = {
    colorFiller.fillCell(drawable, hexagonCell, color)
  }

  override def onCellStateChange(cell: Cell with Polygon): Unit = {
    val color = cell.getState match {
      case cell.State.ALIVE => aliveCellFillColor
      case cell.State.DEAD => deadCellFillColor
    }

    fillCell(cell, color)
  }

}