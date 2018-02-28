package ru.nsu.fit.g15201.boltava.view.toolbar

import ru.nsu.fit.g15201.boltava.view.toolbar.IContract.CellSelectionMode._
import ru.nsu.fit.g15201.boltava.view.toolbar.IContract.{IInteractor, IPresenter, IView}

import scala.util.{Failure, Try}

class ToolbarPresenter(private val view: IView, private val interactor: IInteractor) extends IPresenter {

  {
    view.setPresenter(this)
    // todo: subscribe to interactor updates?
  }

  override def onPlay(): Unit = {
    if (showErrorIfNoModelChosen()) return
    if (showWarningIfGameIsOver()) return
    interactor.getGameController.start()
  }

  override def onPause(): Unit = {
    if (showErrorIfNoModelChosen()) return
    if (showWarningIfGameIsOver()) return
    interactor.getGameController.pause()
  }

  override def onNextStep(): Unit = {
    if (showErrorIfNoModelChosen()) return
    if (showWarningIfGameIsOver()) return
    interactor.getGameController.nextStep()
  }

  override def onReset(): Unit = {
    if (showErrorIfNoModelChosen()) return
    if (showWarningIfGameIsOver()) return
    interactor.getGameController.reset()
  }

  override def onOpenModel(path: String): Unit = {
    Try(interactor.onOpenModel(path)) match {
      case Failure(t) =>
        t.printStackTrace()
        view.showError("Failed to read configuration file", t.getMessage)
      case _ =>
    }
  }

  override def onSaveModel(path: String): Unit = {
    if (interactor.getGameController.isGameRunning) {
      interactor.getGameController.pause()
    }

    Try(interactor.onSaveModel(path)) match {
      case Failure(t) =>
        t.printStackTrace()
        view.showError("Failed to save model configuration file", t.getMessage)
      case _ =>
    }

    if (interactor.getGameController.isGamePaused) {
      interactor.getGameController.start()
    }
  }

  override def onSetReplace(): Unit = {
    interactor.onSetReplace()
    view.setCellSelectionButton(REPLACE)
  }

  override def onSetToggle(): Unit = {
    interactor.onSetToggle()
    view.setCellSelectionButton(TOGGLE)
  }

  override def onOpenSettings(): Unit = {
    interactor.onOpenSettings()
  }

  private def showErrorIfNoModelChosen(): Boolean = {
    var warningShown = false
    if (!interactor.getGameController.isGameInitialized) {
      view.showError(
        "Game model not chosen",
        "Open a game model or create a new one by pressing corresponding toolbar buttons.")
      warningShown = true
    }

    warningShown
  }

  private def showWarningIfGameIsOver(): Boolean = {
    var warningShown = false
    if (interactor.getGameController.isGameFinished ||
        interactor.getGameController.isGameReset) {
      view.showError(
        "Game model not chosen",
        "Open a game model or create a new one by pressing corresponding toolbar buttons.")
      warningShown = true
    }

    warningShown
  }

}
