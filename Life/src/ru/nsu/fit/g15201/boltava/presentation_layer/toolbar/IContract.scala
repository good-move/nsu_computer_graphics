package ru.nsu.fit.g15201.boltava.presentation_layer.toolbar

import ru.nsu.fit.g15201.boltava.domain_layer.logic.IGameLogicController
import ru.nsu.fit.g15201.boltava.presentation_layer.base.{IBasePresenter, IBaseView}
import ru.nsu.fit.g15201.boltava.presentation_layer.toolbar.IContract.CellSelectionMode.CellSelectionMode

object IContract {

  trait IPresenter extends IBasePresenter {

    def onPlay()
    def onPause()
    def onNextStep()
    def onReset()

    def onOpenModel(path: String)
    def onSaveModel(path: String)

    def onSetReplace()
    def onSetToggle()

    def onOpenSettings()

  }

  trait IView extends IBaseView[IPresenter] {

    def showError(title: String, body: String)
    def showWarning(title: String, body: String)
    def showInfo(title: String, body: String)

    def setCellSelectionButton(cellSelectionMode: CellSelectionMode)

  }

  trait IInteractor {

    def onSaveModel(path: String)
    def onOpenModel(path: String)

    def getGameController: IGameLogicController

    def onSetReplace()
    def onSetToggle()

    def onOpenSettings()

  }

  object CellSelectionMode extends Enumeration {
    type CellSelectionMode = Value
    val REPLACE, TOGGLE, NONE = Value
  }

}
