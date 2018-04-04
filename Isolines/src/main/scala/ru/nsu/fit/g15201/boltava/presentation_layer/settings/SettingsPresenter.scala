package ru.nsu.fit.g15201.boltava.presentation_layer.settings

import ru.nsu.fit.g15201.boltava.domain_layer.logic.settings.FieldParameters
import ru.nsu.fit.g15201.boltava.presentation_layer.AlertHelper
import ru.nsu.fit.g15201.boltava.presentation_layer.settings.Contract.{ISettingsInteractor, ISettingsPresenter}
import scalafx.scene.control.TextField
import scalafx.scene.input.MouseEvent
import scalafx.stage.Stage
import scalafxml.core.macros.sfxml

import scala.util.{Failure, Success, Try}

@sfxml
class SettingsPresenter(gridWidthField: TextField,
                        gridHeightField: TextField,
                        lowerXBound: TextField,
                        upperXBound: TextField,
                        lowerYBound: TextField,
                        upperYBound: TextField,
                        interactor: ISettingsInteractor,
                        stage: Stage) extends ISettingsPresenter {

  private var ownStage: Stage = _

  {
    interactor.setPresenter(this)
    interactor.currentParameters match {
      case Some(parameters) =>
        setInitialFieldValues(parameters)
      case _ =>
        setDefaultValues()
    }

  }


  def onCancel(): Unit = {
    close()
  }

  def onApply(): Unit = {
    applyParameters()
  }

  def onOk(): Unit = {
    applyParameters()
    close()
  }

  private def close(): Unit = {
    ownStage.close()
  }

  private def applyParameters(): Unit = {
    Try(collectFieldParameters()) match {
      case Success(parameters) =>
        Try(interactor.applyParameters(parameters)) match {
          case Failure(throwable) =>
            AlertHelper.showError(stage, "Cannot apply parameters", throwable.getMessage)
          case _ =>
        }
      case Failure(throwable) =>
        AlertHelper.showError(stage, "Invalid parameters", throwable.getMessage)
    }
  }

  private def setDefaultValues(): Unit = {
    setInitialFieldValues(FieldParameters(2, 2, 0, 0, 0, 0))
  }

  private def setInitialFieldValues(params: FieldParameters): Unit = {
    gridWidthField.text = params.xNodes.toString
    gridHeightField.text = params.yNodes.toString

    lowerXBound.text = params.lowerXBound.toString
    upperXBound.text = params.upperXBound.toString

    lowerYBound.text = params.lowerYBound.toString
    upperYBound.text = params.upperYBound.toString
  }

  private def collectFieldParameters(): FieldParameters = {
    def tryToNumber[T](field: TextField)(implicit converter: String => T): T = {
      Try(converter(field.text.value.trim)) match {
        case Success(value) => value
        case Failure(_) => throw new Exception(s"Invalid parameter value: ${field.text.value}")
      }
    }

    def toInt(field: TextField): Int = tryToNumber[Int](field)((s: String) => s.toInt)
    def toDouble(field: TextField): Double = tryToNumber[Double](field)((s: String) => s.toDouble)

    FieldParameters(
      toInt(gridWidthField),
      toInt(gridHeightField),
      toDouble(lowerXBound),
      toDouble(upperXBound),
      toDouble(lowerYBound),
      toDouble(upperYBound)
    )
  }

  override def setOwnStage(stage: Stage): Unit = {
    ownStage = stage
  }

}
