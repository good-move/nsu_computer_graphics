package ru.nsu.fit.g15201.boltava.view.base

trait IBaseView[PresenterType <: IBasePresenter] {

  def setPresenter(presenter: PresenterType)

}
