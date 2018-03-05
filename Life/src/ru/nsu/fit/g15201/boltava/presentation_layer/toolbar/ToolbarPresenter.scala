package ru.nsu.fit.g15201.boltava.presentation_layer.toolbar

import javafx.stage.FileChooser
import javafx.stage.FileChooser.ExtensionFilter

import ru.nsu.fit.g15201.boltava.domain_layer.logic.ConfigManager
import ru.nsu.fit.g15201.boltava.presentation_layer.toolbar.IContract.CellSelectionMode._
import ru.nsu.fit.g15201.boltava.presentation_layer.toolbar.IContract.{IInteractor, IPresenter, IView}

import scala.util.{Failure, Try}

class ToolbarPresenter(private val view: IView, private val interactor: IInteractor) extends IPresenter {

  {
    view.setPresenter(this)
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

  override def onOpenModel(): Unit = {
    view.showOpenFileChooser(getProperFileChooser("Open Game Model"), (path: String) => {
      Try(interactor.onOpenModel(path)) match {
        case Failure(t) =>
          t.printStackTrace()
          view.showError("Failed to read configuration file", t.getMessage)
        case _ =>
      }
    })
  }

  override def onSaveModel(): Unit = {
    if (interactor.getGameController.isGameRunning) {
      interactor.getGameController.pause()
    }

    view.showSaveFileChooser(getProperFileChooser(""), (path: String) => {
      Try(interactor.onSaveModel(Some(path))) match {
        case Failure(t) =>
          t.printStackTrace()
          view.showError("Failed to save model configuration file", t.getMessage)
        case _ =>
      }
    })

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

  def getProperFileChooser(title: String): FileChooser = {
    val fileChooser: FileChooser = new FileChooser()
    fileChooser.setTitle(title)
    fileChooser.getExtensionFilters.add(new ExtensionFilter(
      s"${ConfigManager.MODEL_FILE_DESCRIPTION}", s"*.${ConfigManager.MODEL_FILE_EXTENSION}"
    ))
    fileChooser
  }

  override def onClose(): Unit = {
    if (interactor.shouldSavePlaygroundState()) {
      view.showOfferSaveModel()
    } else {
      view.close()
    }
    interactor.finish()
  }

  override def onAgreeSaveModel(): Unit = {
    Try(interactor.onSaveModel(None)) match {
      case Failure(_) =>
        view.showWarning("File not found", "No model file has been opened. Choose file to save model to.")
        view.showSaveFileChooser(getProperFileChooser("Select Model File"), (path: String) => {
          interactor.onSaveModel(Some(path))
        })
      case _ =>
    }
    view.close()
  }

  override def onToggleImpactScores(): Unit = {
    interactor.toggleImpactScores()
  }

  override def onRejectSaveModel(): Unit = {}

}
