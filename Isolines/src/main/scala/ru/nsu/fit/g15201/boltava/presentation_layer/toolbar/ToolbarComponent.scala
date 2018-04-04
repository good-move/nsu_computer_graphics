package ru.nsu.fit.g15201.boltava.presentation_layer.toolbar

import ru.nsu.fit.g15201.boltava.presentation_layer.base.ControlledFxmlComponent
import ru.nsu.fit.g15201.boltava.presentation_layer.menu.Contract.{IMenuInteractor, IMenuPresenter}
import scalafx.stage.Stage
import scalafxml.core.DependenciesByType

import scala.reflect.io.Path
import scala.reflect.runtime.universe.typeOf

class ToolbarComponent(interactor: IMenuInteractor)(implicit stage: Stage) extends ControlledFxmlComponent[IMenuPresenter](
  ToolbarComponent.viewSource,
  Some(ToolbarComponent.cssSource),
  Some(new DependenciesByType(Map(
    typeOf[IMenuInteractor] -> interactor,
    typeOf[Stage] -> stage
  )))
)


object ToolbarComponent {

  def apply(interactor: IMenuInteractor)(implicit stage: Stage): ToolbarComponent = new ToolbarComponent(interactor)

  private val viewSource = Path("src/main/resources/layouts/toolbar_layout.fxml").toURL
  private val cssSource = Path("").toURL

}
