package ru.nsu.fit.g15201.boltava.presentation_layer.menu

import java.io.File

import ru.nsu.fit.g15201.boltava.presentation_layer.AlertHelper
import ru.nsu.fit.g15201.boltava.presentation_layer.menu.Contract.{FileChooserCallback, IMenuInteractor, IMenuPresenter}
import scalafx.scene.control.{ChoiceDialog, Slider, TextField}
import scalafx.scene.image.Image
import scalafx.scene.layout.VBox
import scalafx.stage.FileChooser.ExtensionFilter
import scalafx.stage.{FileChooser, Stage}
import scalafxml.core.macros.sfxml


@sfxml
class MenuPresenter(stage: Stage, interactor: IMenuInteractor) extends IMenuPresenter {

  private def fileToUri(file: File) = s"${file.toURI.toString}"

  override def onOpenImage(): Unit = {
    showOpenFile(createFileChooser("Open image")) { file =>
      val uri = fileToUri(file)
      println(s"Opening $uri")
      interactor.onImageOpened(new Image(uri))
    }
  }

  override def onSaveImage(): Unit = {
    showSaveFile(createFileChooser("Save image")) { file =>
      val uri = fileToUri(file)
      println(s"Saving to $uri")
    }
  }

  private def createFileChooser(title: String): FileChooser = {
    val fileChooser = new FileChooser()
    fileChooser.setTitle(title)
    fileChooser
      .extensionFilters
      .add(new ExtensionFilter("All", interactor.getValidImageExtensions.map(e => s"*.${e.extension}")))
    interactor
      .getValidImageExtensions
      .foreach { extension =>
        fileChooser.extensionFilters.add(
          new ExtensionFilter(extension.extension.toUpperCase , s"*.${extension.extension}")
        )
      }
    fileChooser.initialDirectory = new File(".").getAbsoluteFile
    fileChooser
  }

  private def showSaveFile(fileChooser: FileChooser)(callback: FileChooserCallback): Unit = {
    val file = fileChooser.showSaveDialog(stage)
    if (file != null) {
      callback(file)
    }
  }

  private def showOpenFile(fileChooser: FileChooser)(callback: FileChooserCallback): Unit = {
    val file = fileChooser.showOpenDialog(stage)
    if (file != null) {
      callback(file)
    }
  }

  override def getStage: Stage = stage

  override def onExit(): Unit = {
//    interactor.beforeExit()
    getStage.close()
  }

  override def onNegativeChosen(): Unit = {
    applyIfPossible(() => interactor.applyNegativeFilter())
  }

  override def onGrayScaleChosen(): Unit = {
    applyIfPossible(() => interactor.applyGrayScaleFilter())
  }

  override def onEdgeDetectionChosen(): Unit = {
    if (!interactor.canApplyFilter) {
      showNoImageChosenError()
      return
    }

    val choices = interactor.getKernelsList

    val dialog = new ChoiceDialog(defaultChoice = choices.head, choices = choices) {
      initOwner(stage)
      title = "Edge Detection Configuration"
      contentText = "Choose filter kernel:"
    }

    val selectedKernel = dialog.showAndWait()

    selectedKernel match {
      case Some(kernel) => interactor.applyEdgeDetectionKernel(kernel)
      case None =>
    }

  }

  override def onDoubleUpscale(): Unit = {
    applyIfPossible(() => interactor.applyDoubleUpscale())
  }

  private def showNoImageChosenError(): Unit = {
    AlertHelper.showError(
      stage,
      "Cannot apply filter",
      "Choose an image and select and area to modify before applying filters"
    )
  }

  def onEmbossChosen(): Unit = {
    applyIfPossible(() => interactor.applyEmbossFilter())
  }

  def onSharpenChosen(): Unit = {
    applyIfPossible(() => interactor.applySharpenFilter())
  }

  def onMedianChosen(): Unit = {
    val medianFilterSettings = showMedianFilterSettings()
    if (medianFilterSettings.isDefined) {
      applyIfPossible(() => interactor.applyMedianFilter(medianFilterSettings.get))
    }
  }

  def onContourFilterChosen(): Unit = {
    applyIfPossible(() => interactor.applyContourFilter())
  }

  def onWaterColorFilterChosen(): Unit = {
    val medianFilterSettings = showMedianFilterSettings()
    if (medianFilterSettings.isDefined) {
      applyIfPossible(() => interactor.applyWaterColorFilter(medianFilterSettings.get))
    }
  }

  def applyIfPossible(function: () => Unit): Unit = {
    if (interactor.canApplyFilter) {
      function()
    } else {
      showNoImageChosenError()
    }
  }

  private def showMedianFilterSettings(): Option[Int] = {
    val neighborsCount = Array.range(3,15).map(a => a*a)
    val dialog = new ChoiceDialog[Int](neighborsCount.head, neighborsCount)
    dialog.showAndWait()
  }

  def onRotateImage(): Unit = {
    if (!interactor.canApplyFilter) {
      showNoImageChosenError()
      return
    }

    val slider = new Slider(-180, 180, 0)
    val textBox = new TextField()

    slider.showTickLabels = true
    slider.blockIncrement = 1
    slider.majorTickUnit = 1.0
    slider.value.onChange { (_, _, newValue) =>
      val rotationAngle = newValue.asInstanceOf[Double].toInt
      textBox.text = rotationAngle.toString
      interactor.applyImageRotation(rotationAngle)
    }

    textBox.text.onChange { (_,_, newValue) =>
      val startsWithMinus = newValue.trim.headOption.getOrElse("") == '-'
      val filtered = newValue.trim.filter(char => char.isDigit)
      val finalText = if (startsWithMinus) s"-$filtered" else filtered
      textBox.text = finalText
      if (filtered.nonEmpty) {
        slider.value = finalText.toInt
      }
    }

    val box = new VBox(children = Seq(slider, textBox):_*)

    val dialog = new ChoiceDialog()
    dialog.dialogPane.value.setContent(box)
    dialog.showAndWait()
  }

  def onGammaCorrectionChosen(): Unit = {
    if (!interactor.canApplyFilter) {
      showNoImageChosenError()
      return
    }

    val slider = new Slider(0, 5, 1)
    val textBox = new TextField()

    slider.showTickLabels = true
    slider.blockIncrement = 1
    slider.majorTickUnit = .5
    slider.value.onChange { (_, _, newValue) =>
      val gamma = newValue.asInstanceOf[Double]
      textBox.text = gamma.toString
      interactor.applyGammaCorrection(gamma)
    }

    textBox.text.onChange { (_,_, newValue) =>
      val dotPosition = newValue.trim.indexOf('.')
      val filtered = newValue.trim.filter(char => char.isDigit)
      val finalText = if (dotPosition >= 0) {
        val (first, second) = filtered.splitAt(dotPosition)
        s"$first.$second"
      } else filtered
      textBox.text = finalText
      if (filtered.nonEmpty) {
        slider.value = finalText.toDouble
      }
    }

    val box = new VBox(children = Seq(slider, textBox):_*)

    val dialog = new ChoiceDialog()
    dialog.dialogPane.value.setContent(box)
    dialog.headerText = "Choose gamma correction value"
    dialog.graphic = null
    dialog.title = "Gamma correction"
    dialog.showAndWait()
  }

}
