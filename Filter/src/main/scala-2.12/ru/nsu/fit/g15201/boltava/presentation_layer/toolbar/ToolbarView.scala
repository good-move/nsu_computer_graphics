package ru.nsu.fit.g15201.boltava.presentation_layer.toolbar

import ru.nsu.fit.g15201.boltava.presentation_layer.toolbar.Contract.{FileChooserCallback, IToolbarPresenter, IToolbarView}
import scalafx.scene.control.ToolBar
import scalafx.scene.input.MouseEvent
import scalafx.stage.FileChooser
import scalafxml.core.macros.sfxml

@sfxml
class ToolbarView(toolbar: ToolBar) extends IToolbarView {

  private var presenter: Option[IToolbarPresenter] = None

  def onOpenFile(mouseEvent: MouseEvent): Unit = {
    presenter.foreach(_.onOpenImage())
  }

  override def setPresenter(presenter: IToolbarPresenter): Unit = {
    this.presenter = Some(presenter)
  }

}
