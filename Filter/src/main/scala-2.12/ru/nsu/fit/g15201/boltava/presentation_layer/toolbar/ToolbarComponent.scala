package ru.nsu.fit.g15201.boltava.presentation_layer.toolbar


import ru.nsu.fit.g15201.boltava.presentation_layer.extension.DefaultFxmlComponent
import ru.nsu.fit.g15201.boltava.presentation_layer.toolbar.Contract.IToolbarView

import scala.reflect.io.Path

class ToolbarComponent extends DefaultFxmlComponent[IToolbarView](
  ToolbarComponent.viewSource,
  Some(ToolbarComponent.styleSource)
)

object ToolbarComponent {

  private val viewSource = Path("src/main/resources/layouts/toolbar_view.fxml").toURL
  private val styleSource = Path("src/main/resources/styles/toolbar.css").toURL

  def apply(): ToolbarComponent = new ToolbarComponent()

}