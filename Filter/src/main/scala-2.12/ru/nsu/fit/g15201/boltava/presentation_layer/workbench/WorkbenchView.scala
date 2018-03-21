package ru.nsu.fit.g15201.boltava.presentation_layer.workbench

import ru.nsu.fit.g15201.boltava.domain_layer.geometry.DoublePoint
import ru.nsu.fit.g15201.boltava.domain_layer.geometry.Point._
import ru.nsu.fit.g15201.boltava.presentation_layer.workbench.Contract.{IWorkbenchPresenter, IWorkbenchView}
import scalafx.Includes._
import scalafx.scene.Cursor
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.input.MouseEvent
import scalafx.scene.layout.{AnchorPane, BorderPane}
import scalafxml.core.macros.sfxml

@sfxml
class WorkbenchView(
                     val mainImage: ImageView,
                     val croppedImage: ImageView,
                     val filteredImage: ImageView,
                     val selectionBoxWrapper: AnchorPane,
                     val selectionBox: BorderPane,
                   )
                extends IWorkbenchView {

  private var minSelectionBoxWidth = 0
  private var minSelectionBoxHeight = 0
  private var draggingSelectionBox = false

  private var pressCoordinates: DoublePoint = (.0, .0)

  {
    selectionBox.visible = false
    AnchorPane.setTopAnchor(selectionBox, 100)
    AnchorPane.setLeftAnchor(selectionBox, 200)

    selectionBox.onMouseEntered = _ => {
      if (!draggingSelectionBox) {
        setCursor(Cursor.Hand)
      }
    }

    selectionBox.onMouseExited = _ => {
      if (!draggingSelectionBox) {
        setCursor(Cursor.Default)
      }
    }

    selectionBoxWrapper.onMousePressed = (mouseEvent: MouseEvent) => {
      if (!selectionBox.visible.value) {
        val coordinates = (mouseEvent.x, mouseEvent.y)
        val selectionBoxDimensions: DoublePoint = (selectionBox.width.value, selectionBox.height.value)
        limitedMoveSelectionBox(coordinates - selectionBoxDimensions / 2)
        selectionBox.visible = true
      }
    }

    selectionBox.onMousePressed = (mouseEvent: MouseEvent) => {
      setCursor(Cursor.Move)
      pressCoordinates = (mouseEvent.x, mouseEvent.y)
      draggingSelectionBox = true
    }

    selectionBox.onMouseReleased = (_) => {
      setCursor(Cursor.Default)
      draggingSelectionBox = false
    }

    selectionBox.onMouseDragged = (mouseEvent: MouseEvent) => {
      val nextCoordinates = clampPoint(calculateSelectionCoordinates((mouseEvent.x, mouseEvent.y)))
      limitedMoveSelectionBox(nextCoordinates)
      val selectionBottomX = nextCoordinates.x + selectionBox.width.value
      val selectionBottomY = nextCoordinates.y + selectionBox.height.value
      presenter.get.onImagePartSelected(nextCoordinates, (selectionBottomX, selectionBottomY))
    }

  }

  private def setCursor(cursor: Cursor): Unit = {
    presenter.get.getStage.getScene.setCursor(cursor)
  }

  private var presenter: Option[IWorkbenchPresenter] = None

  override def setMainImage(image: Image): Unit = {
    mainImage.image = image
  }

  override def setCroppedImage(image: Image): Unit = {
    croppedImage.image = image
  }

  override def setFilteredImage(image: Image): Unit = {
    filteredImage.image = image
  }

  override def setPresenter(presenter: IWorkbenchPresenter): Unit = {
    this.presenter = Some(presenter)
  }

  override def setSelectionBoxParameters(minWidth: Int, minHeight: Int): Unit = {
    minSelectionBoxHeight = minHeight
    minSelectionBoxWidth = minWidth
    selectionBox.prefWidth = minWidth
    selectionBox.prefHeight = minHeight
  }

  private def calculateSelectionCoordinates(dragCoordinates: DoublePoint): DoublePoint = {
    val curX = AnchorPane.getLeftAnchor(selectionBox)
    val curY = AnchorPane.getTopAnchor(selectionBox)
    val shift = dragCoordinates - pressCoordinates
      (
      curX + shift.x,
      curY + shift.y,
    )
  }

  private def clampPoint(doublePoint: DoublePoint): DoublePoint = {
    val width = selectionBox.width.value
    val height = selectionBox.height.value

    val clampedX = doublePoint.x.max(0).min(selectionBoxWrapper.width.value - width)
    val clampedY = doublePoint.y.max(0).min(selectionBoxWrapper.height.value - height)
    (clampedX, clampedY)
  }

  private def limitedMoveSelectionBox(point: DoublePoint): Unit =  {
    moveSelectionBox(clampPoint(point))
  }

  private def moveSelectionBox(point: DoublePoint): Unit =  {
    AnchorPane.setLeftAnchor(selectionBox, point.x)
    AnchorPane.setTopAnchor(selectionBox, point.y)
  }

}
