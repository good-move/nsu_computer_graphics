package ru.nsu.fit.g15201.boltava

import javafx.application.Application

import ru.nsu.fit.g15201.boltava.view.LifeGame

object Main {

  def main(args: Array[String]): Unit = {
    Application.launch(classOf[LifeGame], args: _*)
  }

}