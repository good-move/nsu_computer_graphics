package ru.nsu.fit.g15201.boltava.presentation_layer.extension

import scalafx.stage.{Stage, Window}

trait IBaseActivity[T <: IActivityContext] {

  def launch(stage: Stage, context: Option[T] = None)
  def getWindow: Window

}
