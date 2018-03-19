package ru.nsu.fit.g15201.boltava.presentation_layer.extension

import ru.nsu.fit.g15201.boltava.presentation_layer.base.{IBasePresenter, IBaseView}
import scalafxml.core.FXMLLoader

trait IPresenterFxmlComponent[T <: IBasePresenter] extends IComponent {

  def loader: FXMLLoader
  def presenter: T

}
