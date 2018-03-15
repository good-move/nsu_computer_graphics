package ru.nsu.fit.g15201.boltava.presentation_layer.extension

import ru.nsu.fit.g15201.boltava.presentation_layer.base.IBaseView

import scalafxml.core.FXMLLoader

trait IFxmlComponent[T <: IBaseView[_]] extends IComponent {

  def loader: FXMLLoader
  def view: T

}
