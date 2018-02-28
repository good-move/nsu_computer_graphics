package ru.nsu.fit.g15201.boltava

import javafx.application.Application
import javafx.stage.Stage

import ru.nsu.fit.g15201.boltava.presentation_layer.main.MainActivity


object LifeGame {

  def main(args: Array[String]): Unit = {
    Application.launch(classOf[LifeGame], args: _*)
  }

}

class LifeGame extends Application {

  override def start(primaryStage: Stage): Unit = {
    MainActivity.launch(primaryStage)
  }

}
