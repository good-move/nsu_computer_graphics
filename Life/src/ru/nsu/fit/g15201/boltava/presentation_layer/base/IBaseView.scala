package ru.nsu.fit.g15201.boltava.presentation_layer.base

trait IBaseView[PresenterType <: IBasePresenter] {

  def setPresenter(presenter: PresenterType)

}
