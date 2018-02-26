package ru.nsu.fit.g15201.boltava.view.settings

import javafx.beans.value.{ChangeListener, ObservableValue}
import javafx.fxml.FXML
import javafx.scene.control.{Button, Slider, TextField}
import javafx.scene.input.MouseEvent
import javafx.stage.Stage

import ru.nsu.fit.g15201.boltava.model.logic.{BoundsSettings, GameSettings}
import ru.nsu.fit.g15201.boltava.view.settings.IContract.{IPresenter, IView}
import ru.nsu.fit.g15201.boltava.view.settings.utils._

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
  private var boundsSettings: BoundsSettings = new BoundsSettings

  private var presenter: IPresenter = _

  @FXML
  def initialize(): Unit = {
    initSlidersListeners()
    initTextFieldsListeners()
    initButtonListeners()
  }

  override def setBoundsSettings(boundsSettings: BoundsSettings): Unit = {
    this.boundsSettings = boundsSettings
    applyBounds()
  }

  override def setGridSettings(gridSettings: GameSettings): Unit = {
    currentGameSettings = GameSettings(gridSettings)
    applySettings()
  }

  override def getGridSettings: GameSettings = currentGameSettings

  override def setPresenter(presenter: IPresenter): Unit = this.presenter = presenter

  private def applyBounds(): Unit = {
    cellBorderSizeSlider.setMin(boundsSettings.minBorderSize)
    cellBorderSizeSlider.setMax(boundsSettings.maxBorderSize)

    cellBorderWidthSlider.setMin(boundsSettings.minBorderWidth)
    cellBorderWidthSlider.setMax(boundsSettings.maxBorderWidth)
  }

  private def applySettings(): Unit = {
    gridWidthTF.setText(currentGameSettings.width.toString)
    gridHeightTF.setText(currentGameSettings.height.toString)

    cellBorderWidthTF.setText(currentGameSettings.borderWidth.toString)
    cellBorderWidthSlider.setValue(currentGameSettings.borderWidth)

    cellBorderSizeTF.setText(currentGameSettings.borderSize.toString)
    cellBorderSizeSlider.setValue(currentGameSettings.borderSize)
  }

  private def initSlidersListeners(): Unit = {
    cellBorderWidthSlider.valueProperty().addListener(new ChangeListener[Number] {
      override def changed(observable: ObservableValue[_ <: Number], oldValue: Number, nextWidth: Number): Unit = {
        val width = nextWidth.asInstanceOf[Double]
        if (width.isWithinBounds(boundsSettings.minBorderWidth, boundsSettings.maxBorderWidth)) {
          cellBorderWidthTF.setText(width.toString)
          currentGameSettings.borderWidth = width.toInt
        }
      }
    })

    cellBorderWidthTF.onTextChanged(borderWidth => {
      if (isValidInt(borderWidth, boundsSettings.minBorderWidth, boundsSettings.maxBorderWidth)) {
        cellBorderWidthSlider.setValue(borderWidth.toDouble)
        currentGameSettings.borderWidth = borderWidth.toInt
      }
    })

    cellBorderSizeSlider.valueProperty().addListener(new ChangeListener[Number] {
      override def changed(observable: ObservableValue[_ <: Number], oldValue: Number, nextSize: Number): Unit = {
        val nextDoubleValue = nextSize.asInstanceOf[Double]
        if (boundsSettings.minBorderWidth < nextDoubleValue &&
          nextDoubleValue < boundsSettings.maxBorderWidth) {
          cellBorderSizeTF.setText(nextDoubleValue.toString)
          currentGameSettings.borderSize = nextDoubleValue.toInt
        }
      }
    })

    cellBorderSizeTF.onTextChanged(borderSize => {
      if (isValidInt(borderSize, boundsSettings.minBorderSize, boundsSettings.maxBorderSize)) {
        cellBorderSizeSlider.setValue(borderSize.toDouble)
        currentGameSettings.borderSize = borderSize.toInt
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
    println("TF")
    println("is null: " + (gridWidthTF == null))
    gridWidthTF.onTextChanged(gridWidth => {
      if (isValidInt(gridWidth, 1, boundsSettings.maxGridSize)) {
        cellBorderSizeSlider.setValue(gridWidth.toInt)
        currentGameSettings.width = gridWidth.toInt
      }
    })

    gridHeightTF.onTextChanged(gridHeight => {
      if (isValidInt(gridHeight, 1, boundsSettings.maxGridSize)) {
        cellBorderSizeSlider.setValue(gridHeight.toInt)
        currentGameSettings.height = gridHeight.toInt
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

    okButton.setOnMouseClicked((event: MouseEvent) => {
      if (presenter != null) {
        presenter.onOkClicked()
      }
      closeWindow(okButton)
    })

    cancelButton.setOnMouseClicked((event: MouseEvent) => {
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
      s.forall(c => c.isDigit)
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