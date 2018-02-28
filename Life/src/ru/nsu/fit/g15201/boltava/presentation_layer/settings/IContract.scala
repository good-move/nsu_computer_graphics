package ru.nsu.fit.g15201.boltava.presentation_layer.settings

import ru.nsu.fit.g15201.boltava.domain_layer.logic.settings.{GameSettings, SettingsBounds}
import ru.nsu.fit.g15201.boltava.presentation_layer.base.{IBasePresenter, IBaseView}

object IContract {

  trait IPresenter extends IBasePresenter {

    def onApplyClicked()
    def onOkClicked()
    def onCancelClicked()

  }

  trait IView extends IBaseView[IPresenter] {

    def setBoundsSettings(boundsSettings: SettingsBounds)
    def setGridSettings(gridSettings: GameSettings)
    def getGridSettings: GameSettings
    def showError(title: String, message: String)
    def close()

  }

  trait IInteractor {

    def getGameSettings: GameSettings
    def getSettingsBounds: SettingsBounds
    def applyGameSettings(gridSettings: GameSettings)

  }

}
