package ru.nsu.fit.g15201.boltava

import javafx.application.Application
import javafx.scene.{Group, Scene}
import javafx.scene.image.{ImageView, WritableImage}
import javafx.scene.input.MouseEvent
import javafx.scene.paint.Color
import javafx.stage.Stage

import ru.nsu.fit.g15201.boltava.model.canvas._
import ru.nsu.fit.g15201.boltava.model.graphics.{BresenhamLineCreator, IColorFiller, ScanLineFiller}
import ru.nsu.fit.g15201.boltava.model.logic.Hexagon

class LifeGame extends Application {

  private var drawable: IDrawable = _
  private var gridController: IGridController[Hexagon] = _
  private var cellGrid: Array[Array[Hexagon]] = _
  private var colorFiller: IColorFiller[Hexagon] = _

  private var scene: Scene = _

  override def start(primaryStage: Stage): Unit = {
    // TODO:
    // read config file
    // create and resize field
    // init stage and UI controls
    // create hexagons and mark them as alive or dead
    // draw hexagons

    initStage(primaryStage)
    initHexagonGrid()
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
    for (i <- hex.vertices.indices) {
      val linePoints = BresenhamLineCreator.getLinePoints(hex.vertices(i), hex.vertices((i + 1) % hex.vertices.length))
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

  private def initHexagonGrid() = {
    gridController = new HexagonalGridController(50)
    cellGrid = gridController.generateGrid(10, 7)
  }

  private def drawHexagonGrid(): Unit = {
    cellGrid.foreach(_.foreach(hexagon => drawHex(drawable, hexagon)))
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
    val hexCoords = gridController.getCellByPoint(point)
    if (hexCoords.x < 0 || hexCoords.y < 0) return
    //      println(s"($x, $y) -> $hexCoords")
    colorFiller.fillCell(drawable, cellGrid(hexCoords.x)(hexCoords.y), Color.GRAY)
  }

}