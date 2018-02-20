package ru.nsu.fit.g15201.boltava

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.paint.Color
import javafx.scene.{Parent, Scene}
import javafx.stage.Stage


object LifeGame {

  def main(args: Array[String]): Unit = {
    Application.launch(classOf[LifeGame], args: _*)
  }

}

class LifeGame extends Application {

  private val windowTitle = "Conway Game Of Life"

  override def start(primaryStage: Stage): Unit = {
    val root: Parent = FXMLLoader.load(getClass.getResource("./view/main_view.fxml"))
    primaryStage.setTitle(windowTitle)
    primaryStage.setScene(new Scene(root, 800, 500, Color.WHITE))
    primaryStage.show()
  }

}
