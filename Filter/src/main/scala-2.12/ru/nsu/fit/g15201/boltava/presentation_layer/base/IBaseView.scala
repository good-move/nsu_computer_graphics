package ru.nsu.fit.g15201.boltava.presentation_layer.base

trait IBaseView[T <: IBasePresenter] {

  def setPresenter(presenter: T)

}
