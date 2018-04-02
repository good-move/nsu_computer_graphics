package ru.nsu.fit.g15201.boltava.presentation_layer.menu

import ru.nsu.fit.g15201.boltava.presentation_layer.base.ControlledFxmlComponent
import ru.nsu.fit.g15201.boltava.presentation_layer.menu.Contract.{IMenuInteractor, IMenuPresenter}
import scalafx.stage.Stage
import scalafxml.core.DependenciesByType

import scala.reflect.io.Path
import scala.reflect.runtime.universe.typeOf

class MenuComponent(interactor: IMenuInteractor)(implicit stage: Stage) extends ControlledFxmlComponent[IMenuPresenter](
  MenuComponent.viewSource,
  Some(MenuComponent.cssSource),
  Some(new DependenciesByType(Map(
    typeOf[IMenuInteractor] -> interactor,
    typeOf[Stage] -> stage
  )))
)

object MenuComponent {

  def apply(interactor: IMenuInteractor)(implicit stage: Stage): MenuComponent = new MenuComponent(interactor)

  private val viewSource = Path("src/main/resources/layouts/menu_layout.fxml").toURL
  private val cssSource = Path("").toURL

}
