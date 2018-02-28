package ru.nsu.fit.g15201.boltava.presentation_layer.settings

import javafx.beans.value.{ChangeListener, ObservableValue}
import javafx.fxml.FXML
import javafx.scene.control.{Button, Slider, TextField}
import javafx.scene.input.MouseEvent
import javafx.stage.Stage

import ru.nsu.fit.g15201.boltava.domain_layer.logic.settings._
import ru.nsu.fit.g15201.boltava.presentation_layer.settings.IContract.{IPresenter, IView}
import ru.nsu.fit.g15201.boltava.presentation_layer.settings.utils._

class SettingsPaneView extends IView {

  @FXML var gridWidthTF: TextField = _
  @FXML var gridHeightTF: TextField = _

  @FXML var cellBorderSizeTF: TextField = _
  @FXML var cellBorderSizeSlider: Slider = _

  @FXML var cellBorderWidthTF: TextField = _
  @FXML var cellBorderWidthSlider: Slider = _

  @FXML var minBirthTF: TextField = _
  @FXML var maxBirthTF: TextField = _

  @FXML var minAliveTF: TextField = _
  @FXML var maxAliveTF: TextField = _

  @FXML var cancelButton: Button = _
  @FXML var applyButton: Button = _
  @FXML var okButton: Button = _

  private var currentGameSettings = new GameSettings
  private var settingsBounds = new SettingsBounds

  private var presenter: IPresenter = _

  @FXML
  def initialize(): Unit = {
    initSlidersListeners()
    initTextFieldsListeners()
    initButtonListeners()
  }

  override def setBoundsSettings(boundsSettings: SettingsBounds): Unit = {
    this.settingsBounds = boundsSettings
    applyBounds()
  }

  override def setGridSettings(gameSettings: GameSettings): Unit = {
    currentGameSettings = gameSettings.copy()
    applyPlaygroundSettings(currentGameSettings.playgroundSettings)
    applyLifeScores(currentGameSettings.lifeScores)
    applyImpactScores(currentGameSettings.impactScores)
  }

  override def getGridSettings: GameSettings = currentGameSettings

  override def setPresenter(presenter: IPresenter): Unit = this.presenter = presenter

  private def applyBounds(): Unit = {
    cellBorderSizeSlider.setMin(settingsBounds.minBorderSize)
    cellBorderSizeSlider.setMax(settingsBounds.maxBorderSize)
    setSliderProps(cellBorderSizeSlider)

    cellBorderWidthSlider.setMin(settingsBounds.minBorderWidth)
    cellBorderWidthSlider.setMax(settingsBounds.maxBorderWidth)
    setSliderProps(cellBorderWidthSlider)
  }

  private def setSliderProps(slider: Slider): Unit = {
    slider.setShowTickMarks(true)
    slider.setShowTickLabels(true)
    slider.setMajorTickUnit(5)
  }

  private def applyImpactScores(impactScores: ImpactScores): Unit = {

  }

  private def applyLifeScores(lifeScores: LifeScores): Unit = {
    minBirthTF.setText(lifeScores.minBirthScore.toString)
    maxBirthTF.setText(lifeScores.maxBirthScore.toString)
    minAliveTF.setText(lifeScores.minAliveScore.toString)
    maxAliveTF.setText(lifeScores.maxAliveScore.toString)
  }

  private def applyPlaygroundSettings(playgroundSettings: PlaygroundSettings): Unit = {
    gridWidthTF.setText(playgroundSettings.gridWidth.toString)
    gridHeightTF.setText(playgroundSettings.gridHeight.toString)

    cellBorderWidthTF.setText(playgroundSettings.borderWidth.toString)
    cellBorderWidthSlider.setValue(playgroundSettings.borderWidth)

    cellBorderSizeTF.setText(playgroundSettings.borderSize.toString)
    cellBorderSizeSlider.setValue(playgroundSettings.borderSize)
  }

  private def initSlidersListeners(): Unit = {
    cellBorderWidthSlider.valueProperty().addListener(new ChangeListener[Number] {
      override def changed(observable: ObservableValue[_ <: Number], oldValue: Number, nextWidth: Number): Unit = {
        val width = nextWidth.asInstanceOf[Double].toInt
        cellBorderWidthSlider.setValue(width)
        if (width.isWithinBounds(settingsBounds.minBorderWidth, settingsBounds.maxBorderWidth)) {
          cellBorderWidthTF.setText(width.toString)
          currentGameSettings.playgroundSettings.borderWidth = width
        }
      }
    })

    cellBorderWidthTF.onTextChanged(borderWidth => {
      if (isValidInt(borderWidth, settingsBounds.minBorderWidth, settingsBounds.maxBorderWidth)) {
        cellBorderWidthSlider.setValue(borderWidth.toDouble)
        currentGameSettings.playgroundSettings.borderWidth = borderWidth.toInt
      }
    })

    cellBorderSizeSlider.valueProperty().addListener(new ChangeListener[Number] {
      override def changed(observable: ObservableValue[_ <: Number], oldValue: Number, nextSize: Number): Unit = {
        val borderSize = nextSize.asInstanceOf[Double].toInt
        if (borderSize.isWithinBounds(settingsBounds.minBorderSize, settingsBounds.maxBorderSize)) {
          cellBorderSizeTF.setText(borderSize.toString)
          currentGameSettings.playgroundSettings.borderSize = borderSize
        }
      }
    })

    cellBorderSizeTF.onTextChanged(borderSize => {
      if (isValidInt(borderSize, settingsBounds.minBorderSize, settingsBounds.maxBorderSize)) {
        cellBorderSizeSlider.setValue(borderSize.toDouble)
        currentGameSettings.playgroundSettings.borderSize = borderSize.toDouble.toInt
      }
    })

  }

  private def isValidInt(intString: String, lowerBound: Int, upperBound: Int): Boolean = {
    val trimmedString = intString.trim()
    trimmedString.length > 0 &&
      trimmedString.isNonNegativeInteger &&
      trimmedString.toDouble.isWithinBounds(lowerBound, upperBound)
  }

  private def initTextFieldsListeners(): Unit = {
    gridWidthTF.onTextChanged(gridWidth => {
      if (isValidInt(gridWidth, 1, settingsBounds.maxGridSize)) {
        currentGameSettings.playgroundSettings.gridWidth = gridWidth.toDouble.toInt
      }
    })

    gridHeightTF.onTextChanged(gridHeight => {
      if (isValidInt(gridHeight, 1, settingsBounds.maxGridSize)) {
        currentGameSettings.playgroundSettings.gridHeight = gridHeight.toDouble.toInt
      }
    })

    val setValueOrShowError = (valueSetter: Double => Unit) => {
      (value: String) => {
        if (value.isNonNegativeInteger) {
          valueSetter(value.toDouble)
        } else {
          // show error
        }
      }
    }

    minBirthTF.onTextChanged(setValueOrShowError(currentGameSettings.lifeScores.minBirthScore_=))
    maxBirthTF.onTextChanged(setValueOrShowError(currentGameSettings.lifeScores.maxBirthScore_=))
    minAliveTF.onTextChanged(setValueOrShowError(currentGameSettings.lifeScores.minAliveScore_=))
    maxAliveTF.onTextChanged(setValueOrShowError(currentGameSettings.lifeScores.maxAliveScore_=))
  }

  private def initButtonListeners(): Unit = {

    applyButton.setOnMouseClicked(_ => {
      if (presenter != null) {
        presenter.onApplyClicked()
      }
    })

    okButton.setOnMouseClicked((_: MouseEvent) => {
      if (presenter != null) {
        presenter.onOkClicked()
      }
      closeWindow(okButton)
    })

    cancelButton.setOnMouseClicked((_: MouseEvent) => {
      if (presenter != null) {
        presenter.onCancelClicked()
      }
      closeWindow(cancelButton)
    })

  }

  private def closeWindow(button: Button): Unit = {
    button.getScene.getWindow.asInstanceOf[Stage].close()
  }

}

object utils {

  implicit class StringExtension(val s: String) {
    def isNonNegativeInteger: Boolean = {
      s.length > 0 && s.forall(c => c.isDigit)
    }
  }

  implicit class DoubleExtension(val n: Double) {
    def isWithinBounds(lowerBound: Double, upperBound: Double): Boolean = {
      lowerBound <= n && n <= upperBound
    }
  }

  implicit class TextFieldWrapper(val textField: TextField) {
    def onTextChanged(handler: String => Unit): Unit = {
      textField.textProperty().addListener(new ChangeListener[String] {
        override def changed(observable: ObservableValue[_ <: String], oldValue: String, newValue: String): Unit = {
          handler(newValue)
        }
      })
    }
  }

}