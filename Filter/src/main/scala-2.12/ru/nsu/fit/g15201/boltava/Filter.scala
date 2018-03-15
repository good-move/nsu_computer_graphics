package ru.nsu.fit.g15201.boltava

import ru.nsu.fit.g15201.boltava.presentation_layer.main_activity.{MainActivity, MainContext}

import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage



object Filter extends JFXApp {

  stage = new PrimaryStage {
    title = "Filter"
  }

  MainActivity.launch()(stage)

}