package ru.nsu.fit.g15201.boltava.presentation_layer.menu

import ru.nsu.fit.g15201.boltava.presentation_layer.extension.PresenterFxmlComponent
import ru.nsu.fit.g15201.boltava.presentation_layer.menu.Contract.{IMenuInteractor, IMenuPresenter}
import scalafx.scene.Parent
import scalafx.stage.Stage
import scalafxml.core.DependenciesByType
import scala.reflect.runtime.universe.typeOf

import scala.reflect.io.Path

class MenuComponent(interactor: IMenuInteractor)(implicit stage: Stage) extends PresenterFxmlComponent[IMenuPresenter] (
  MenuComponent.viewSource,
  Some(MenuComponent.cssSource),
  Some(new DependenciesByType(Map(
    typeOf[IMenuInteractor] -> interactor,
    typeOf[Stage] -> stage
  )))
)

object MenuComponent {

  def apply(interactor: IMenuInteractor)(implicit stage: Stage): MenuComponent = new MenuComponent(interactor)

  private val viewSource = Path("src/main/resources/layouts/menu_view.fxml").toURL
  private val cssSource = Path("").toURL

}
