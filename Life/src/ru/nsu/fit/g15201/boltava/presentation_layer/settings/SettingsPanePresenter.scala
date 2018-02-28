package ru.nsu.fit.g15201.boltava.presentation_layer.settings

import ru.nsu.fit.g15201.boltava.presentation_layer.settings.IContract.{IInteractor, IPresenter, IView}

class SettingsPanePresenter(private val view: IView, private val interactor: IInteractor) extends IPresenter {

  {
    view.setPresenter(this)
    view.setBoundsSettings(interactor.getSettingsBounds)
    view.setGridSettings(interactor.getGameSettings)
  }

  override def onApplyClicked(): Unit = {
    interactor.applyGameSettings(view.getGridSettings)
  }

  override def onOkClicked(): Unit = {
    interactor.applyGameSettings(view.getGridSettings)
  }

  override def onCancelClicked(): Unit = {}

}
