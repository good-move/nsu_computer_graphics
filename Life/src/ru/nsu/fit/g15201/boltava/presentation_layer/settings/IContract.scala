package ru.nsu.fit.g15201.boltava.presentation_layer.settings

import ru.nsu.fit.g15201.boltava.domain_layer.logic.{BoundsSettings, GameSettings}
import ru.nsu.fit.g15201.boltava.presentation_layer.base.{IBasePresenter, IBaseView}

object IContract {

  trait IPresenter extends IBasePresenter {

    def onApplyClicked()
    def onOkClicked()
    def onCancelClicked()

  }

  trait IView extends IBaseView[IPresenter] {

    def setBoundsSettings(boundsSettings: BoundsSettings)
    def setGridSettings(gridSettings: GameSettings)
    def getGridSettings: GameSettings

  }

  trait IInteractor {

    def getGameSettings: GameSettings
    def getBoundsSettings: BoundsSettings

    def applyGameSettings(gridSettings: GameSettings)

  }

}
