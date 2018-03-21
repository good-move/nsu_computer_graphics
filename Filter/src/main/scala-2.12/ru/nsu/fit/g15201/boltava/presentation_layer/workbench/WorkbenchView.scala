package ru.nsu.fit.g15201.boltava.presentation_layer.workbench

import ru.nsu.fit.g15201.boltava.domain_layer.geometry.Point._
import ru.nsu.fit.g15201.boltava.domain_layer.geometry.{DoubleDimensions, DoublePoint}
import ru.nsu.fit.g15201.boltava.presentation_layer.workbench.Contract.{IWorkbenchPresenter, IWorkbenchView}
import ru.nsu.fit.g15201.boltava.presentation_layer.workbench.ResizeBorder.ResizeBorder
import scalafx.Includes._
import scalafx.scene.Cursor
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.input.MouseEvent
import scalafx.scene.layout.{AnchorPane, BorderPane}
import scalafxml.core.macros.sfxml

object ResizeBorder extends Enumeration {
  type ResizeBorder = Value
  val Top, Right, Bottom, Left,
  TopLeft, TopRight, BottomRight, BottomLeft = Value
}

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
  private val resizeAreSize = 5
  private var scaleFactor = 1.0

  private var resizeDirection: Option[ResizeBorder] = None
  private var resizeEnabled = false

  private var dragAnchor: DoublePoint = (.0, .0)
  private var resizeAnchor: DoublePoint = (.0, .0)

  private var cachedSelectionBoxDimensions = DoubleDimensions(0,0)
  private var cachedSelectionBoxOffset: DoublePoint = (.0, .0)

  private def resizeSelectionBox(resizeBorder: ResizeBorder, dragCoordinates: (Double, Double)): Unit = {
    val shift = dragCoordinates - resizeAnchor
    val boxWidth = cachedSelectionBoxDimensions.width
    val boxHeight = cachedSelectionBoxDimensions.height


    resizeBorder match {
      case ResizeBorder.Top =>
        setSelectionBoxDimensions(boxWidth, boxHeight - shift.y)
        limitedMoveSelectionBox((cachedSelectionBoxOffset.x, cachedSelectionBoxOffset.y + shift.y))
      case ResizeBorder.Right =>
        setSelectionBoxDimensions(boxWidth + shift.x, boxHeight)
      case ResizeBorder.Bottom => setSelectionBoxDimensions(boxWidth, boxHeight + shift.y)
      case ResizeBorder.Left =>
        setSelectionBoxDimensions(boxWidth - shift.x, boxHeight)
        limitedMoveSelectionBox((cachedSelectionBoxOffset.x + shift.x, cachedSelectionBoxOffset.y))

      case ResizeBorder.TopLeft =>
        setSelectionBoxDimensions(boxWidth - shift.x, boxHeight - shift.y)
        limitedMoveSelectionBox((cachedSelectionBoxOffset.x +shift.x, cachedSelectionBoxOffset.y + shift.y))
      case ResizeBorder.TopRight =>
        limitedMoveSelectionBox((cachedSelectionBoxOffset.x, cachedSelectionBoxOffset.y + shift.y))
        setSelectionBoxDimensions(boxWidth + shift.x, boxHeight - shift.y)
      case ResizeBorder.BottomRight =>
        setSelectionBoxDimensions(boxWidth + shift.x, boxHeight + shift.y)
      case ResizeBorder.BottomLeft =>
        setSelectionBoxDimensions(boxWidth - shift.x, boxHeight + shift.y)
        limitedMoveSelectionBox((cachedSelectionBoxOffset.x + shift.x, cachedSelectionBoxOffset.y))
    }
  }

  {
    selectionBox.visible = false

    selectionBoxWrapper.onMousePressed = (mouseEvent: MouseEvent) => {
      if (!selectionBox.visible.value) {
        val coordinates = (mouseEvent.x, mouseEvent.y)
        val selectionBoxDimensions: DoublePoint = (selectionBox.width.value, selectionBox.height.value)
        limitedMoveSelectionBox(coordinates - selectionBoxDimensions / 2)
        selectionBox.visible = true
      }
    }

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

    selectionBox.onMousePressed = (mouseEvent: MouseEvent) => {
      setCursor(Cursor.Move)
      dragAnchor = (mouseEvent.x, mouseEvent.y)
      cachedSelectionBoxDimensions = DoubleDimensions(selectionBox.width.value, selectionBox.height.value)
      cachedSelectionBoxOffset = getSelectionBoxOffset
      resizeAnchor = (mouseEvent.sceneX, mouseEvent.sceneY)
      draggingSelectionBox = true
    }

    selectionBox.onMouseReleased = (_) => {
      setCursor(Cursor.Default)
      draggingSelectionBox = false
    }

    selectionBox.onMouseDragged = (mouseEvent: MouseEvent) => {
      val dragCoordinates = (mouseEvent.x, mouseEvent.y)
      if (resizeEnabled) {
        resizeSelectionBox(resizeDirection.get, (mouseEvent.sceneX, mouseEvent.sceneY))
        val nextOffset = getSelectionBoxOffset
        val selectionBottomX = nextOffset.x + selectionBox.width.value
        val selectionBottomY = nextOffset.y + selectionBox.height.value
        presenter.get.onImagePartSelected(nextOffset, (selectionBottomX, selectionBottomY))
      } else {
        val nextOffset = clampPoint(calculateSelectionBoxCoordinates(countDragShift(dragCoordinates)))
        moveSelectionBox(nextOffset)
        val selectionBottomX = nextOffset.x + selectionBox.prefWidth.value
        val selectionBottomY = nextOffset.y + selectionBox.prefHeight.value
        presenter.get.onImagePartSelected(nextOffset, (selectionBottomX, selectionBottomY))
      }
    }

    selectionBox.onMouseMoved = (mouseEvent: MouseEvent) => {
      if (!draggingSelectionBox) {
        val mousePoint = (mouseEvent.x, mouseEvent.y)
        val direction = getResizeDirection(mousePoint)
        resizeDirection = direction
        setCursor(getResizeCursor(direction).getOrElse(Cursor.Hand))
        if (direction.isDefined) {
          resizeEnabled = true
        } else {
          resizeEnabled = false
        }
      }
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
    setSelectionBoxDimensions(minWidth, minHeight)
  }

  private def calculateSelectionBoxCoordinates(shift: DoublePoint): DoublePoint = {
    getSelectionBoxOffset + shift
  }

  private def getSelectionBoxOffset: DoublePoint =  {
    val xOffset = AnchorPane.getLeftAnchor(selectionBox)
    val yOffset = AnchorPane.getTopAnchor(selectionBox)
    DoublePoint(xOffset, yOffset)
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


  def getResizeDirection(mouse: DoublePoint): Option[ResizeBorder] = {
    val boxWidth = selectionBox.width.value
    val boxHeight = selectionBox.height.value

    var activeBorders = 0

    if (0 <= mouse.x && mouse.x <= resizeAreSize) {
      activeBorders |= 1
    }
    if (boxWidth - resizeAreSize <= mouse.x && mouse.x <= boxWidth ) {
      activeBorders |= 2
    }
    if (0 <= mouse.y && mouse.y <= resizeAreSize) {
      activeBorders |= 4
    }
    if (boxHeight - resizeAreSize <= mouse.y && mouse.y <= boxHeight) {
      activeBorders |= 8
    }

    activeBorders match {
      case 1 => Some(ResizeBorder.Left)
      case 2 => Some(ResizeBorder.Right)
      case 4 => Some(ResizeBorder.Top)
      case 8 => Some(ResizeBorder.Bottom)

      case 5 => Some(ResizeBorder.TopLeft)
      case 9 => Some(ResizeBorder.BottomLeft)

      case 6 => Some(ResizeBorder.TopRight)
      case 10 => Some(ResizeBorder.BottomRight)

      case _ => None
    }
  }

  private def getResizeCursor(resizeDirection: Option[ResizeBorder]): Option[Cursor] = {
    resizeDirection match {
      case Some(ResizeBorder.Top) => Some(Cursor.NResize)
      case Some(ResizeBorder.Right) => Some(Cursor.EResize)
      case Some(ResizeBorder.Bottom) => Some(Cursor.SResize)
      case Some(ResizeBorder.Left) => Some(Cursor.WResize)

      case Some(ResizeBorder.TopLeft) => Some(Cursor.NWResize)
      case Some(ResizeBorder.TopRight) => Some(Cursor.NEResize)
      case Some(ResizeBorder.BottomRight) => Some(Cursor.SEResize)
      case Some(ResizeBorder.BottomLeft) => Some(Cursor.SWResize)
      case _ => None
    }
  }

  private def countDragShift(point: DoublePoint): DoublePoint = point - dragAnchor

  private def setSelectionBoxDimensions(width: Double, height: Double): Unit = {
    selectionBox.prefWidth = width
    selectionBox.prefHeight = height
    presenter.get.onSelectionBoxSizeChanged(DoubleDimensions(selectionBox.width.value, selectionBox.height.value))
  }

  override def setCroppedImageScaleFactor(factor: Double): Unit = {
    scaleFactor = factor
  }

}
