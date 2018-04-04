package ru.nsu.fit.g15201.boltava.presentation_layer.settings

import ru.nsu.fit.g15201.boltava.domain_layer.logic.settings.FieldParameters
import ru.nsu.fit.g15201.boltava.presentation_layer.base.{IBaseInteractor, IBasePresenter}
import scalafx.stage.Stage

object Contract {

  trait ISettingsPresenter extends IBasePresenter {
    def setOwnStage(stage: Stage)
  }

  trait ISettingsInteractor extends IBaseInteractor[ISettingsPresenter] {

    def applyParameters(parameters: FieldParameters): Unit
    def currentParameters: Option[FieldParameters]

  }


}
