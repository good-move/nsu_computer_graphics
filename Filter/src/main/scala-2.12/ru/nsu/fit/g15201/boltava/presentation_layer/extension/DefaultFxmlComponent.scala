package ru.nsu.fit.g15201.boltava.presentation_layer.extension

import java.net.URL

import ru.nsu.fit.g15201.boltava.presentation_layer.base.IBaseView

import scalafx.Includes._
import scalafx.scene.Parent
import scalafxml.core.{FXMLLoader, NoDependencyResolver}

abstract class DefaultFxmlComponent[T <: IBaseView[_]](viewSource: URL) extends IFxmlComponent[T] {

  protected var _root: Parent = _
  protected var _loader: FXMLLoader = _
  protected var _view: T = _

  {
    _loader = new FXMLLoader(viewSource, NoDependencyResolver)
    _root = _loader.load[javafx.scene.Parent]
    _view = _loader.getController[T]
  }

  override def loader: FXMLLoader = _loader

  override def view: T = _view

  override def root: Parent = _root

}
