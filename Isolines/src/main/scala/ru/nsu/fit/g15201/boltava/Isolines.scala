package ru.nsu.fit.g15201.boltava

import ru.nsu.fit.g15201.boltava.domain_layer.logic.function._
import ru.nsu.fit.g15201.boltava.domain_layer.logic.settings.ConfigReader
import ru.nsu.fit.g15201.boltava.domain_layer.mesh.{IsolineDetector, MeshGenerator}
import ru.nsu.fit.g15201.boltava.domain_layer.primitives.{Dimensions, Point3D}
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import scalafx.scene.canvas.Canvas
import scalafx.scene.layout.VBox
import scalafx.scene.paint.Color


object Isolines extends JFXApp {

  val settings = ConfigReader.read("config")
  val function = new EllipticHyperboloid()
  function.domain = FiniteDomain2D(-10, 10, -10, 10)
  val fieldDimensions = Dimensions(500, 500)

  val cellGrid = MeshGenerator.generate(fieldDimensions, settings, function)
//
  val canvas = new Canvas(fieldDimensions.width, fieldDimensions.height)

  val vBox = new VBox(children = canvas)

  def drawGrid(image: Canvas, cellGrid: MeshGenerator.CellGrid) = {

    def drawNode(position: Point3D): Unit = {
      image.graphicsContext2D.stroke = Color.Black
      image.graphicsContext2D.lineWidth = 1
      image.graphicsContext2D.strokeOval(position.x.toInt, position.y.toInt, 5, 5)
    }

    for {
      y <- 0 until cellGrid.cellsY
      x <- 0 until cellGrid.cellsX
    } {
      val cell = cellGrid(x, y)
      drawNode(cell.topLeft.position)
      drawNode(cell.topRight.position)
      drawNode(cell.bottomRight.position)
      drawNode(cell.bottomLeft.position)
    }
  }
//
//  drawGrid(canvas, cellGrid)
  val isolineDetector = new IsolineDetector
  val isoLevels = isolineDetector.calculateIsoLevels(1, Math.sqrt(201), settings.levels)
  println(s"IsoLevels: $isoLevels")
  cellGrid.grid.foreach { cell =>
    isolineDetector.buildIsolines(cell, isoLevels)
  }

  def drawIsolines(canvas: Canvas, isolineDetector: IsolineDetector): Unit = {
    val writer = canvas.graphicsContext2D
    writer.lineWidth = 1
    isolineDetector.isolines.foreach { segment =>
      writer.strokeLine(segment.start.x, segment.start.y, segment.end.x, segment.end.y)
    }
  }

  drawIsolines(canvas, isolineDetector)


  val scene = new Scene(vBox)
  stage = new PrimaryStage
  stage.scene = scene
  stage.show()
}

