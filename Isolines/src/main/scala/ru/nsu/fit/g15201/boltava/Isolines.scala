package ru.nsu.fit.g15201.boltava

import ru.nsu.fit.g15201.boltava.domain_layer.controllers.MenuInteractor
import ru.nsu.fit.g15201.boltava.domain_layer.logic.function._
import ru.nsu.fit.g15201.boltava.domain_layer.logic.settings.ConfigReader
import ru.nsu.fit.g15201.boltava.domain_layer.mesh.{IsolineDetector, MeshGenerator}
import ru.nsu.fit.g15201.boltava.domain_layer.primitives.{Dimensions, Point3D}
import ru.nsu.fit.g15201.boltava.presentation_layer.menu.MenuComponent
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import scalafx.scene.canvas.Canvas
import scalafx.scene.layout.{Priority, StackPane}
import scalafx.scene.paint.Color


object Isolines extends JFXApp {

  val interactor = new MenuInteractor
  val menu = MenuComponent(interactor)

  val settings = ConfigReader.read("config")
  val function = new SinCosProduct()
  function.domain = FiniteDomain2D(-1, 1, -1, 1)
  val fieldDimensions = Dimensions(500, 300)

  val cellGrid = MeshGenerator.generate(fieldDimensions, settings, function)
  val canvas = new Canvas(fieldDimensions.width, fieldDimensions.height)
  val stackPane = new StackPane {
    children = canvas
  }

  def drawGrid(image: Canvas, cellGrid: MeshGenerator.CellGrid) = {

    def drawNode(position: Point3D): Unit = {
      image.graphicsContext2D.stroke = Color.Black
      image.graphicsContext2D.lineWidth = 1
      image.graphicsContext2D.strokeOval(position.x, position.y, 5, 5)
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

//  drawGrid(canvas, cellGrid)
  val isolineDetector = new IsolineDetector
  val isoLevels = isolineDetector.calculateIsoLevels(-1, 1, settings.levels)


  println(s"IsoLevels: $isoLevels")
  cellGrid.grid.foreach { cell =>
    isolineDetector.buildIsolines(cell, isoLevels)
  }

  def drawIsolines(canvas: Canvas, isolineDetector: IsolineDetector): Unit = {
    val gc = canvas.graphicsContext2D
    gc.lineWidth = 1
    isolineDetector.isolines.foreach { segment =>
      gc.strokeLine(segment.start.x, segment.start.y, segment.end.x, segment.end.y)
    }
  }

  drawIsolines(canvas, isolineDetector)
  canvas.hgrow = Priority.Always
  canvas.vgrow = Priority.Always

  stackPane.prefWidth = fieldDimensions.width
  stackPane.prefHeight = fieldDimensions.height
  canvas.width <== stackPane.width
  canvas.height <== stackPane.height

  stackPane.width.onChange { (_, _, width) =>
    if (width.doubleValue() > 0 && stackPane.height.value > 0) {
      val fieldDimensions = Dimensions(width.doubleValue(), stackPane.height.value)
      val cells = MeshGenerator.generate(fieldDimensions, settings, function)
      isolineDetector.clearIsolines()
      cells.grid.foreach { cell =>
        isolineDetector.buildIsolines(cell, isoLevels)
      }
      canvas.graphicsContext2D.clearRect(0,0,canvas.width.value, canvas.height.value)
      drawIsolines(canvas, isolineDetector)
    }
  }

  stackPane.height.onChange { (_, _, height) =>
    if (height.doubleValue() > 0 && stackPane.width.value > 0) {
      isolineDetector.clearIsolines()
      val fieldDimensions = Dimensions(stackPane.width.value ,height.doubleValue())
      val cells = MeshGenerator.generate(fieldDimensions, settings, function)
      cells.grid.foreach { cell =>
        isolineDetector.buildIsolines(cell, isoLevels)
      }
      drawIsolines(canvas, isolineDetector)
    }
  }




  val scene = new Scene(stackPane)
  stage = new PrimaryStage
  stage.scene = scene
  stage.show()
}

