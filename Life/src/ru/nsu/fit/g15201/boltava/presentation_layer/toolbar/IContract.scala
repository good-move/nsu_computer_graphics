package ru.nsu.fit.g15201.boltava.presentation_layer.toolbar

import javafx.stage.FileChooser

import ru.nsu.fit.g15201.boltava.domain_layer.logic.IGameLogicController
import ru.nsu.fit.g15201.boltava.presentation_layer.base.{IBasePresenter, IBaseView}
import ru.nsu.fit.g15201.boltava.presentation_layer.toolbar.IContract.CellSelectionMode.CellSelectionMode

object IContract {

  trait IPresenter extends IBasePresenter {

    def onPlay()
    def onPause()
    def onNextStep()
    def onReset()

    def onOpenModel()
    def onSaveModel()
    def onAgreeSaveModel()

    def onSetReplace()
    def onSetToggle()

    def onOpenSettings()

    def onClose()
  }

  trait IView extends IBaseView[IPresenter] {

    def showError(title: String, body: String)
    def showWarning(title: String, body: String)
    def showInfo(title: String, body: String)
    def showSaveFileChooser(fileChooser: FileChooser, onFileChosen: String => Unit)
    def showOpenFileChooser(fileChooser: FileChooser, onFileChosen: String => Unit)
    def setCellSelectionButton(cellSelectionMode: CellSelectionMode)
    def showOfferSaveModel(): Unit

  }

  trait IInteractor {

    def onSaveModel(path: Option[String])
    def onOpenModel(path: String)
    def shouldSavePlaygroundState(): Boolean

    def getGameController: IGameLogicController

    def onSetReplace()
    def onSetToggle()

    def onOpenSettings()
    def finish()

  }

  object CellSelectionMode extends Enumeration {
    type CellSelectionMode = Value
    val REPLACE, TOGGLE, NONE = Value
  }

}
