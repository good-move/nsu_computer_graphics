package ru.nsu.fit.g15201.boltava.presentation_layer.workbench

import ru.nsu.fit.g15201.boltava.domain_layer.geometry.DoublePoint
import Contract.{IWorkbenchPresenter, IWorkbenchView}


class WorkbenchPresenter(view: IWorkbenchView) extends IWorkbenchPresenter {

  {
    view.setPresenter(this)
  }

  override def onImagePartSelected(topLeft: DoublePoint, bottomRight: DoublePoint): Unit = ???

  override def onImageSelectionMoved(topLeft: DoublePoint, bottomRight: DoublePoint): Unit = ???

}
