package ru.nsu.fit.g15201.boltava.view

import java.net.URL
import javafx.fxml.FXMLLoader
import javafx.scene.{Parent, Scene}
import javafx.stage.{Modality, Stage, Window}

abstract class CustomModalDialog[T] (
          private val title: String,
          private val cssPath: String,
          private val contentPath: URL,
          private val owner: Window = null
                                ) extends Stage {


  private var viewController: T = _

  {
    val loader = new FXMLLoader(contentPath)
    val content: Parent = loader.load()
    viewController = loader.getController[T]()
    val scene = new Scene(content)
    scene.getStylesheets.add(cssPath)
    setScene(scene)
    setResizable(false)
    setTitle("About")
    initOwner(owner)
    initModality(Modality.APPLICATION_MODAL)
  }

  def getController: T = {
    viewController
  }

}
