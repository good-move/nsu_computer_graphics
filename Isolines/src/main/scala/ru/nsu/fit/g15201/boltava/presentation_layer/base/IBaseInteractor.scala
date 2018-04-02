package ru.nsu.fit.g15201.boltava.presentation_layer.base

trait IBaseInteractor[P <: IBasePresenter] {

  def setPresenter(presenter: P)

}
