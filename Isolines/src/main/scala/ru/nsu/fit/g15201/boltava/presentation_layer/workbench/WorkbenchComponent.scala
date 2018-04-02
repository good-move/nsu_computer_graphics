package ru.nsu.fit.g15201.boltava.presentation_layer.workbench

import ru.nsu.fit.g15201.boltava.presentation_layer.base.ControlledFxmlComponent
import ru.nsu.fit.g15201.boltava.presentation_layer.workbench.Contract.{IWorkbenchInteractor, IWorkbenchPresenter}
import scalafx.stage.Stage
import scalafxml.core.DependenciesByType

import scala.reflect.runtime.universe.typeOf
import scala.reflect.io.Path

class WorkbenchComponent(interactor: IWorkbenchInteractor)(implicit stage: Stage) extends ControlledFxmlComponent[IWorkbenchPresenter](
  WorkbenchComponent.viewSource,
  Some(WorkbenchComponent.cssSource),
  Some(new DependenciesByType(Map(
    typeOf[IWorkbenchInteractor] -> interactor,
    typeOf[Stage] -> stage
  )))
)


object WorkbenchComponent {

  def apply(interactor: IWorkbenchInteractor)(implicit stage: Stage): WorkbenchComponent = new WorkbenchComponent(interactor)

  private val viewSource = Path("src/main/resources/layouts/workbench_layout.fxml").toURL
  private val cssSource = Path("src/main/resources/styles/workbench.css").toURL

}