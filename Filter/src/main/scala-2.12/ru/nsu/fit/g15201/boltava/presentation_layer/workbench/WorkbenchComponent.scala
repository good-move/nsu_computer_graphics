package ru.nsu.fit.g15201.boltava.presentation_layer.workbench

import ru.nsu.fit.g15201.boltava.presentation_layer.extension.{DefaultFxmlComponent, IFxmlComponent}
import ru.nsu.fit.g15201.boltava.presentation_layer.workbench.Contract.IWorkbenchView

import scala.reflect.io.Path


class WorkbenchComponent extends DefaultFxmlComponent[IWorkbenchView](WorkbenchComponent.viewSource)


object WorkbenchComponent {

  private val viewSource = Path("src/main/resources/layouts/workbench_view.fxml").toURL

  def apply(): WorkbenchComponent = new WorkbenchComponent()

}
