package ru.nsu.fit.g15201.boltava.presentation_layer.toolbar

import ru.nsu.fit.g15201.boltava.presentation_layer.menu.Contract.{IMenuInteractor, IMenuPresenter}
import ru.nsu.fit.g15201.boltava.presentation_layer.toolbar.Contract.{IToolbarPresenter, IToolbarView}
import scalafx.stage.Stage


class ToolbarPresenter(private val view: IToolbarView,
                       private val menuPresenter: IMenuPresenter,
                       private val interactor: IMenuInteractor
                      )(implicit stage: Stage) extends IToolbarPresenter {

  {
    view.setPresenter(this)
  }

  override def onOpenImage(): Unit = menuPresenter.onOpenImage()

  override def onSaveImage(): Unit = menuPresenter.onSaveImage()

  override def getStage: Stage = stage

}
