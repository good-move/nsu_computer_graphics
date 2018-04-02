package ru.nsu.fit.g15201.boltava.presentation_layer.base

import scalafxml.core.FXMLLoader

trait IFxmlComponent extends IComponent {

  def loader: FXMLLoader

}
