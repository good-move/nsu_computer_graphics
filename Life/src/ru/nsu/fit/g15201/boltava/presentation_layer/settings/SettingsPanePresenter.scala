package ru.nsu.fit.g15201.boltava.presentation_layer.settings

import ru.nsu.fit.g15201.boltava.domain_layer.logic.GridType
import ru.nsu.fit.g15201.boltava.presentation_layer.settings.IContract.{IInteractor, IPresenter, IView}

import scala.util.{Failure, Success, Try}

class SettingsPanePresenter(private val view: IView, private val interactor: IInteractor) extends IPresenter {

  {
    view.setPresenter(this)
    view.setBoundsSettings(interactor.getSettingsBounds)
    val gameSettings = interactor.getGameSettings
    view.setGridSettings(gameSettings)
    view.setGridTypesList(GridType.values.toSeq)
  }

  override def onApplyClicked(): Unit = {
    tryApplySettings()
  }

  override def onOkClicked(): Unit = {
    tryApplySettings(shouldCloseWindow = true)
  }

  private def tryApplySettings(shouldCloseWindow: Boolean = false): Unit = {
    Try(interactor.applyGameSettings(view.getGridSettings)) match {
      case Success(_) => if (shouldCloseWindow) view.close()
      case Failure(t) => view.showError("Invalid Settings", t.getMessage)
    }
  }

  override def onCancelClicked(): Unit = {
    view.close()
  }

}
