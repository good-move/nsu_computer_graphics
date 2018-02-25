package ru.nsu.fit.g15201.boltava.view.help

import javafx.fxml.{FXML, FXMLLoader}
import javafx.scene.input.MouseEvent
import javafx.scene.{Parent, Scene}
import javafx.stage.{Modality, Stage, Window}

import scala.reflect.io.Path

class HelpDialog(owner: Window = null) extends Stage {
  private val cssPath = Path("styles/help_modal.css").toString()
  private val contentPath = getClass.getResource("./help_modal_dialog.fxml")

  {
    val content: Parent = FXMLLoader.load(contentPath)
    val scene = new Scene(content)
    scene.getStylesheets.add(cssPath)
    setScene(scene)
    setResizable(false)
    setTitle("Help")
    initOwner(owner)
    initModality(Modality.APPLICATION_MODAL)
  }

}
