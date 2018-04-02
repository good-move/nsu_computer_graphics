package ru.nsu.fit.g15201.boltava.presentation_layer.base

trait IControlledFxmlComponent[P <: IBasePresenter] extends IFxmlComponent {

  def presenter: P

}
