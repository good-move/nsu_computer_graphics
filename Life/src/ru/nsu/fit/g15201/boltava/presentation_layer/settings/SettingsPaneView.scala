package ru.nsu.fit.g15201.boltava.presentation_layer.settings

import javafx.beans.value.{ChangeListener, ObservableValue}
import javafx.fxml.FXML
import javafx.scene.control.{Button, Slider, TextField}
import javafx.scene.input.MouseEvent
import javafx.stage.Stage

import ru.nsu.fit.g15201.boltava.domain_layer.logic.settings.{GameSettings, SettingsBounds}
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

  override def setGridSettings(gridSettings: GameSettings): Unit = {
    currentGameSettings = gridSettings.copy()
    applySettings()
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

  private def applySettings(): Unit = {
    gridWidthTF.setText(currentGameSettings.gridWidth.toString)
    gridHeightTF.setText(currentGameSettings.gridHeight.toString)

    cellBorderWidthTF.setText(currentGameSettings.borderWidth.toString)
    cellBorderWidthSlider.setValue(currentGameSettings.borderWidth)

    cellBorderSizeTF.setText(currentGameSettings.borderSize.toString)
    cellBorderSizeSlider.setValue(currentGameSettings.borderSize)
  }

  private def initSlidersListeners(): Unit = {
    cellBorderWidthSlider.valueProperty().addListener(new ChangeListener[Number] {
      override def changed(observable: ObservableValue[_ <: Number], oldValue: Number, nextWidth: Number): Unit = {
        val width = nextWidth.asInstanceOf[Double].toInt
        cellBorderWidthSlider.setValue(width)
        if (width.isWithinBounds(settingsBounds.minBorderWidth, settingsBounds.maxBorderWidth)) {
          cellBorderWidthTF.setText(width.toString)
          currentGameSettings.borderWidth = width
        }
      }
    })

    cellBorderWidthTF.onTextChanged(borderWidth => {
      if (isValidInt(borderWidth, settingsBounds.minBorderWidth, settingsBounds.maxBorderWidth)) {
        cellBorderWidthSlider.setValue(borderWidth.toDouble)
        currentGameSettings.borderWidth = borderWidth.toInt
      }
    })

    cellBorderSizeSlider.valueProperty().addListener(new ChangeListener[Number] {
      override def changed(observable: ObservableValue[_ <: Number], oldValue: Number, nextSize: Number): Unit = {
        val borderSize = nextSize.asInstanceOf[Double].toInt
        if (borderSize.isWithinBounds(settingsBounds.minBorderSize, settingsBounds.maxBorderSize)) {
          cellBorderSizeTF.setText(borderSize.toString)
          currentGameSettings.borderSize = borderSize
        }
      }
    })

    cellBorderSizeTF.onTextChanged(borderSize => {
      if (isValidInt(borderSize, settingsBounds.minBorderSize, settingsBounds.maxBorderSize)) {
        cellBorderSizeSlider.setValue(borderSize.toDouble)
        currentGameSettings.borderSize = borderSize.toDouble.toInt
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
        cellBorderSizeSlider.setValue(gridWidth.toInt)
        currentGameSettings.gridWidth = gridWidth.toDouble.toInt
      }
    })

    gridHeightTF.onTextChanged(gridHeight => {
      if (isValidInt(gridHeight, 1, settingsBounds.maxGridSize)) {
        cellBorderSizeSlider.setValue(gridHeight.toInt)
        currentGameSettings.gridHeight = gridHeight.toDouble.toInt
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

    minBirthTF.onTextChanged(setValueOrShowError(currentGameSettings.minBirthScore_=))
    maxBirthTF.onTextChanged(setValueOrShowError(currentGameSettings.maxBirthScore_=))
    minAliveTF.onTextChanged(setValueOrShowError(currentGameSettings.minAliveScore_=))
    maxAliveTF.onTextChanged(setValueOrShowError(currentGameSettings.maxAliveScore_=))
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