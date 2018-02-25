package ru.nsu.fit.g15201.boltava.view.settings

import javafx.beans.value.{ChangeListener, ObservableValue}
import javafx.beans.{InvalidationListener, Observable}
import javafx.fxml.FXML
import javafx.scene.control.{Button, Slider, TextField}

import ru.nsu.fit.g15201.boltava.model.logic.{BoundsSettings, GridSettings}

import ru.nsu.fit.g15201.boltava.view.settings.utils._

class SettingsPaneController extends IGridParametersProvider {

  private var settingsChangeListener: IGridParametersChangeListener = _

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


  private var currentGridSettings: GridSettings = _
  private var boundsSettings: BoundsSettings = _

  @FXML
  def initialize(): Unit = {
    initSliders()
  }

  def setCurrentSettings(gridSettings: GridSettings): Unit = {
    currentGridSettings = GridSettings(gridSettings)
    applySettings()
  }

  def setBoundsSettings(boundsSettings: BoundsSettings): Unit = {
    this.boundsSettings = boundsSettings
    applyBounds()
  }

  override def setChangeListener(gridParametersChangeListener: IGridParametersChangeListener): Unit = {
    settingsChangeListener = gridParametersChangeListener
  }

  override def removeChangeListener(): Unit = {
    settingsChangeListener = null
  }

  private def applyBounds(): Unit = {
    cellBorderSizeSlider.setMin(boundsSettings.minCellBorderSize)
    cellBorderSizeSlider.setMax(boundsSettings.maxCellBorderSize)

    cellBorderWidthSlider.setMin(boundsSettings.minCellBorderWidth)
    cellBorderWidthSlider.setMax(boundsSettings.maxCellBorderWidth)
  }

  private def applySettings(): Unit = {
    gridWidthTF.setText(currentGridSettings.width.toString)
    gridHeightTF.setText(currentGridSettings.height.toString)

    cellBorderWidthTF.setText(currentGridSettings.borderWidth.toString)
    cellBorderWidthSlider.setValue(currentGridSettings.borderWidth)

    cellBorderSizeTF.setText(currentGridSettings.cellSideSize.toString)
    cellBorderSizeSlider.setValue(currentGridSettings.cellSideSize)
  }

  private def initSliders(): Unit = {
    cellBorderWidthSlider.valueProperty().addListener(new ChangeListener[Number] {
      override def changed(observable: ObservableValue[_ <: Number], oldValue: Number, nextValue: Number): Unit = {
        val nextIntValue = nextValue.asInstanceOf[Int]
        if (boundsSettings.minCellBorderWidth < nextIntValue &&
            nextIntValue < boundsSettings.maxCellBorderWidth) {
          cellBorderWidthTF.setText(nextIntValue.toString)
        }
      }
    })

    cellBorderWidthTF.textProperty().addListener(new ChangeListener[String] {
      override def changed(observable: ObservableValue[_ <: String], oldValue: String, nextValue: String): Unit = {
        if (nextValue.isNonNegativeInteger) {
          val newValueDouble = nextValue.toDouble
          if (boundsSettings.minCellBorderWidth < newValueDouble && newValueDouble < boundsSettings.maxCellBorderWidth) {
            cellBorderWidthSlider.setValue(newValueDouble)
          }
        }
      }
    })

    cellBorderSizeSlider.valueProperty().addListener(new ChangeListener[Number] {
      override def changed(observable: ObservableValue[_ <: Number], oldValue: Number, nextValue: Number): Unit = {
        val nextIntValue = nextValue.asInstanceOf[Int]
        if (boundsSettings.minCellBorderWidth < nextIntValue &&
          nextIntValue < boundsSettings.maxCellBorderWidth) {
          cellBorderSizeTF.setText(nextIntValue.toString)
        }
      }
    })

    cellBorderSizeTF.textProperty().addListener(new ChangeListener[String] {
      override def changed(observable: ObservableValue[_ <: String], oldValue: String, nextValue: String): Unit = {
        if (nextValue.isNonNegativeInteger) {
          val newValueDouble = nextValue.toDouble
          if (boundsSettings.minCellBorderWidth < newValueDouble && newValueDouble < boundsSettings.maxCellBorderWidth) {
            cellBorderSizeSlider.setValue(newValueDouble)
          }
        }
      }
    })

  }

}


object utils {

  implicit class StringExtension(val s: String) {
    def isNonNegativeInteger: Boolean = {
      s.forall(c => c.isDigit)
    }
  }

}