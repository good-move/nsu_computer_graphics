package ru.nsu.fit.g15201.boltava.view

import javafx.application.{Application, Platform}
import javafx.fxml.{FXML, FXMLLoader}
import javafx.geometry.Pos
import javafx.scene.control.{Button, ToolBar}
import javafx.scene.image.{ImageView, WritableImage}
import javafx.scene.input.MouseEvent
import javafx.scene.layout.GridPane
import javafx.scene.paint.Color
import javafx.scene.{Group, Parent, Scene}
import javafx.scene.canvas.Canvas
import javafx.stage.Stage

import ru.nsu.fit.g15201.boltava.model.canvas.{IGridController, _}
import ru.nsu.fit.g15201.boltava.model.canvas.geometry.{DoublePoint, Point}
import ru.nsu.fit.g15201.boltava.model.graphics.{BresenhamLineCreator, IColorFiller, ScanLineFiller}
import ru.nsu.fit.g15201.boltava.model.logic.{Cell, GameController, State}

class LifeGame extends Application  {

  private val windowTitle = "Conway Game Of Life"

  override def start(primaryStage: Stage): Unit = {
    val root: Parent = FXMLLoader.load(getClass.getResource("form.fxml"))
    primaryStage.setTitle(windowTitle)
    primaryStage.setScene(new Scene(root, 800, 500, Color.WHITE))
    primaryStage.show()
  }

}
