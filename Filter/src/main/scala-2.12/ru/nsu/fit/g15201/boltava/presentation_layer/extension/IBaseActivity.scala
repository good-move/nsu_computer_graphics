package ru.nsu.fit.g15201.boltava.presentation_layer.extension

import scalafx.stage.{Stage, Window}

trait IBaseActivity[T <: IActivityContext] {

  def launch(context: Option[T] = None)(implicit stage: Stage)

}
