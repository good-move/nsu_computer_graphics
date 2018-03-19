package ru.nsu.fit.g15201.boltava.presentation_layer.extension

import java.net.URL

import ru.nsu.fit.g15201.boltava.presentation_layer.base.IBasePresenter
import scalafx.Includes._
import scalafx.scene.Parent
import scalafxml.core.{ControllerDependencyResolver, FXMLLoader, NoDependencyResolver}

abstract class PresenterFxmlComponent[P <: IBasePresenter](
                                      viewSource: URL,
                                      cssSource: Option[URL] = None,
                                      resolver: Option[ControllerDependencyResolver] = None
                                      ) extends IPresenterFxmlComponent[P] {

  protected var _root: Parent = _
  protected var _loader: FXMLLoader = _
  protected var _presenter: P = _

  {
    _loader = new FXMLLoader(viewSource, resolver.getOrElse(NoDependencyResolver))
    _root = _loader.load[javafx.scene.Parent]
    if (cssSource.isDefined) {
      _root.stylesheets.add(cssSource.get.toString)
    }
    _presenter = _loader.getController[P]
  }

  override def loader: FXMLLoader = _loader

  override def root: Parent = _root

  override def presenter: P = _presenter

}
