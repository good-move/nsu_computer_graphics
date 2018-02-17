package ru.nsu.fit.g15201.boltava

import javafx.application.Application
import javafx.scene.{Group, Scene}
import javafx.scene.image.{ImageView, WritableImage}
import javafx.scene.input.MouseEvent
import javafx.scene.paint.Color
import javafx.stage.Stage

import ru.nsu.fit.g15201.boltava.model.GameController
import ru.nsu.fit.g15201.boltava.model.canvas._
import ru.nsu.fit.g15201.boltava.model.graphics.{BresenhamLineCreator, IColorFiller, ScanLineFiller}

class LifeGame extends Application {

  private var drawable: IDrawable = _
  private var colorFiller: IColorFiller = _
  private var gameController: GameController[Hexagon] = _

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

  def drawHex(drawable: IDrawable, hex: Hexagon): Unit = {
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
    gameController = new GameController[Hexagon](width, height, new HexagonalGridController(50))
  }

  private def drawHexagonGrid(): Unit = {
    gameController.getCells.foreach(_.foreach(hexagon => drawHex(drawable, hexagon)))
  }

  private def setEventHandlers(): Unit = {
    scene.addEventHandler(MouseEvent.MOUSE_CLICKED, (event: MouseEvent) => {
      fillHexagon((event.getSceneX, event.getSceneY))
      event.consume()
    })

    scene.addEventHandler(MouseEvent.DRAG_DETECTED, (event: MouseEvent)=> {
      scene.startFullDrag()
      event.consume()
    })

    scene.setOnMouseDragOver((event: MouseEvent) => {
      fillHexagon((event.getSceneX, event.getSceneY))
    })

  }

  private def fillHexagon(point: DoublePoint): Unit = {
    val hexCoords = gameController.getGridController.getCellByPoint(point)
    val cellGrid = gameController.getCells
    if (hexCoords.x < 0 || hexCoords.y < 0 ||
        hexCoords.x >= cellGrid.length || hexCoords.y >= cellGrid(0).length) return
    //      println(s"($x, $y) -> $hexCoords")
    colorFiller.fillCell(drawable, cellGrid(hexCoords.x)(hexCoords.y), Color.GRAY)
  }

}