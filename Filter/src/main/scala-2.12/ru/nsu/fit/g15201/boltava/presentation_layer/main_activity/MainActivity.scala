package ru.nsu.fit.g15201.boltava.presentation_layer.main_activity

import ru.nsu.fit.g15201.boltava.presentation_layer.extension.IBaseActivity
import ru.nsu.fit.g15201.boltava.presentation_layer.toolbar.Contract.IToolbarPresenter
import ru.nsu.fit.g15201.boltava.presentation_layer.toolbar.{ToolbarComponent, ToolbarInteractor, ToolbarPresenter}
import ru.nsu.fit.g15201.boltava.presentation_layer.workbench.Contract.IWorkbenchPresenter
import ru.nsu.fit.g15201.boltava.presentation_layer.workbench.{WorkbenchComponent, WorkbenchPresenter}

import scalafx.scene.Scene
import scalafx.scene.layout.{Priority, VBox}
import scalafx.stage.{Stage, Window}

object MainActivity extends IBaseActivity[MainContext] {

  private var window: Option[Window] = None
  private var workbenchPresenter: Option[IWorkbenchPresenter] = None
  private var toolbarPresenter: Option[IToolbarPresenter] = None

  override def launch(context: Option[MainContext] = None)(implicit stage: Stage): Unit = {
    window = Some(stage)

    val workbench = WorkbenchComponent()
    val toolbar = ToolbarComponent()

    workbenchPresenter = Some(new WorkbenchPresenter(workbench.view))
    toolbarPresenter = Some(new ToolbarPresenter(toolbar.view, new ToolbarInteractor))

    stage.scene = new Scene {
      root = new VBox {
        children = Seq(
          toolbar.root,
          workbench.root
        )
        workbench.root.vgrow = Priority.Always
      }
    }
  }

  override def getWindow: Window = {
    if (window.isEmpty) {
      throw UninitializedFieldError("Window is unavailable")
    }
    window.get
  }

}
