package ru.nsu.fit.g15201.boltava.presentation_layer.workbench

import ru.nsu.fit.g15201.boltava.domain_layer.geometry.DoublePoint
import Contract.{IWorkbenchPresenter, IWorkbenchView}

import scalafx.stage.Window


class WorkbenchPresenter(view: IWorkbenchView)(implicit window: Window) extends IWorkbenchPresenter {

  {
    view.setPresenter(this)
  }

  override def onImagePartSelected(topLeft: DoublePoint, bottomRight: DoublePoint): Unit = ???

  override def onImageSelectionMoved(topLeft: DoublePoint, bottomRight: DoublePoint): Unit = ???

  override def getWindow: Window = window
}
