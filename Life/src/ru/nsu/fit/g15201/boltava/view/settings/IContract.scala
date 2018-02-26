package ru.nsu.fit.g15201.boltava.view.settings

import ru.nsu.fit.g15201.boltava.model.logic.{BoundsSettings, GameSettings}
import ru.nsu.fit.g15201.boltava.view.base.{IBasePresenter, IBaseView}

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
