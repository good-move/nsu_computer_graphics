package ru.nsu.fit.g15201.boltava.presentation_layer.workbench

import ru.nsu.fit.g15201.boltava.domain_layer.transform.TransformableImage
import Contract.{IWorkbenchPresenter, IWorkbenchView}

import scalafx.scene.control.ScrollPane
import scalafx.scene.image.{Image, ImageView, WritableImage}
import scalafxml.core.macros.sfxml

@sfxml
class WorkbenchView(
                   val scrollPane: ScrollPane,
                   val mainImage: ImageView,
                   val croppedImage: ImageView,
                   val filteredImage: ImageView
                   )
                extends IWorkbenchView {

  private var presenter: Option[IWorkbenchPresenter] = None

  {
//    Try(mainImage.image = new Image(Path("images/bmp/Lena.bmp").toString)) match {
//      case Failure(t) => t.printStackTrace()
//      case _ =>
//    }
  }

  override def setMainImage(image: Image): Unit = {
    mainImage.image = image
  }

  override def setCroppedImage(transformableImage: TransformableImage): Unit = {
    croppedImage.image = setImage(transformableImage)
  }

  override def setFilteredImage(transformableImage: TransformableImage): Unit = {
    filteredImage.image = setImage(transformableImage)
  }

  override def setPresenter(presenter: IWorkbenchPresenter): Unit = {
    this.presenter = Some(presenter)
  }

  private def setImage(source: TransformableImage): WritableImage = {
    val image = new WritableImage(source.getWidth, source.getHeight)
    for {
      x <- 0 until image.width.value.toInt
      y <- 0 until image.height.value.toInt
    } {
      image.pixelWriter.setArgb(x, y, source.getFlatContent(x * source.getWidth + y))
    }
    image
  }

}
