package ru.nsu.fit.g15201.boltava.presentation_layer.settings

import ru.nsu.fit.g15201.boltava.presentation_layer.base.ControlledFxmlComponent
import ru.nsu.fit.g15201.boltava.presentation_layer.settings.Contract.{ISettingsInteractor, ISettingsPresenter}
import scalafx.scene.Scene
import scalafx.stage.Stage
import scalafxml.core.DependenciesByType

import scala.reflect.runtime.universe.typeOf
import scala.reflect.io.Path

class SettingsComponent(interactor: ISettingsInteractor)(implicit stage: Stage) extends ControlledFxmlComponent[ISettingsPresenter](
  SettingsComponent.viewSource,
  Some(SettingsComponent.cssSource),
  Some(new DependenciesByType(Map(
    typeOf[ISettingsInteractor] -> interactor,
    typeOf[Stage] -> stage
  )))
)

object SettingsComponent {

  def apply(interactor: ISettingsInteractor)(implicit stage: Stage): SettingsComponent = new SettingsComponent(interactor)

  private val viewSource = Path("src/main/resources/layouts/settings_layout.fxml").toURL
  private val cssSource = Path("").toURL

}
