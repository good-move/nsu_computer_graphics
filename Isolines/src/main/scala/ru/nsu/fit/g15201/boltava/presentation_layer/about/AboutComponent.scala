package ru.nsu.fit.g15201.boltava.presentation_layer.about

import ru.nsu.fit.g15201.boltava.presentation_layer.base.DefaultFxmlComponent
import scalafx.scene.Scene
import scalafx.stage.Stage

import scala.reflect.io.Path

class AboutComponent extends DefaultFxmlComponent(
  AboutComponent.viewSource, Some(AboutComponent.cssSource)
) {

  def showInNewWindow(): Unit = {
    val stage = new Stage {scene = new Scene(root)}
    stage.show()
  }

}

object AboutComponent {

  def apply(): AboutComponent = new AboutComponent

  private val viewSource = Path("src/main/resources/layouts/about_layout.fxml").toURL
  private val cssSource = Path("").toURL

}