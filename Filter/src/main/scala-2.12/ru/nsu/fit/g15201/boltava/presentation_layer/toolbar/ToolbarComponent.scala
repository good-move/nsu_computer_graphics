package ru.nsu.fit.g15201.boltava.presentation_layer.toolbar


import ru.nsu.fit.g15201.boltava.presentation_layer.extension.{DefaultFxmlComponent, IFxmlComponent}
import ru.nsu.fit.g15201.boltava.presentation_layer.toolbar.Contract.IToolbarView

import scala.reflect.io.Path

class ToolbarComponent extends DefaultFxmlComponent[IToolbarView](ToolbarComponent.viewSource)

object ToolbarComponent {

  private val viewSource = Path("src/main/resources/layouts/toolbar_view.fxml").toURL

  def apply(): ToolbarComponent = new ToolbarComponent()

}