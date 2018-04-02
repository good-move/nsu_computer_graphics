package ru.nsu.fit.g15201.boltava.presentation_layer.base

import java.net.URL

import scalafx.Includes._
import scalafx.scene.Parent
import scalafxml.core.{ControllerDependencyResolver, FXMLLoader, NoDependencyResolver}

abstract class DefaultFxmlComponent(viewSource: URL,
                                    cssSource: Option[URL] = None,
                                    resolver: Option[ControllerDependencyResolver] = None
                                   ) extends IFxmlComponent {

  protected var _root: Parent = _
  protected var _loader: FXMLLoader = _

  {
    _loader = new FXMLLoader(viewSource, resolver.getOrElse(NoDependencyResolver))
    _root = _loader.load[javafx.scene.Parent]
    if (cssSource.isDefined) {
      _root.stylesheets.add(cssSource.get.toString)
    }
  }

  override def loader: FXMLLoader = _loader

  override def root: Parent = _root

}