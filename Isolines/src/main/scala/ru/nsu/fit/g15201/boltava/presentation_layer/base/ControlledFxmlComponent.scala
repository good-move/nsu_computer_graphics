package ru.nsu.fit.g15201.boltava.presentation_layer.base

import java.net.URL

import scalafx.Includes._
import scalafx.scene.Parent
import scalafxml.core.{ControllerDependencyResolver, FXMLLoader, NoDependencyResolver}

abstract class ControlledFxmlComponent[P <: IBasePresenter](viewSource: URL,
                                                            cssSource: Option[URL] = None,
                                                            resolver: Option[ControllerDependencyResolver] = None
                                                          ) extends DefaultFxmlComponent(viewSource, cssSource, resolver)
                                                            with IControlledFxmlComponent[P] {
  protected var _presenter: P = _

  {
    _presenter = _loader.getController[P]
  }

  override def presenter: P = _presenter

}