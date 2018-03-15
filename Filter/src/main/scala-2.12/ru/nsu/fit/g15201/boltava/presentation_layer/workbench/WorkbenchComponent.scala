package ru.nsu.fit.g15201.boltava.presentation_layer.workbench

import ru.nsu.fit.g15201.boltava.presentation_layer.extension.DefaultFxmlComponent
import ru.nsu.fit.g15201.boltava.presentation_layer.workbench.Contract.IWorkbenchView

import scala.reflect.io.Path


class WorkbenchComponent extends DefaultFxmlComponent[IWorkbenchView](
  WorkbenchComponent.viewSource,
  Some(WorkbenchComponent.styleSource)
)


object WorkbenchComponent {

  private val viewSource = Path("src/main/resources/layouts/workbench_view.fxml").toURL
  private val styleSource = Path("src/main/resources/styles/workbench.css").toURL

  def apply(): WorkbenchComponent = new WorkbenchComponent()

}
