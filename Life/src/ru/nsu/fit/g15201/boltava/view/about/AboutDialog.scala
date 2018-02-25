package ru.nsu.fit.g15201.boltava.view.about

import javafx.fxml.FXMLLoader
import javafx.scene.{Parent, Scene}
import javafx.stage.{Modality, Stage, Window}

import scala.reflect.io.Path

class AboutDialog(owner: Window = null) extends Stage {
  private val cssPath = Path("styles/about_modal.css").toString()
  private val contentPath = getClass.getResource("./about_modal_dialog.fxml")

  {
    val content: Parent = FXMLLoader.load(contentPath)
    val scene = new Scene(content)
    scene.getStylesheets.add(cssPath)
    setScene(scene)
    setResizable(false)
    setTitle("About")
    initOwner(owner)
    initModality(Modality.APPLICATION_MODAL)
  }

}
