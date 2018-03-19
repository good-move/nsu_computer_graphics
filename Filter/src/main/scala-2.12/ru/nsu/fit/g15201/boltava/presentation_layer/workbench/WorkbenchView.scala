package ru.nsu.fit.g15201.boltava.presentation_layer.workbench

import ru.nsu.fit.g15201.boltava.domain_layer.filter.RawImage
import ru.nsu.fit.g15201.boltava.presentation_layer.workbench.Contract.{IWorkbenchPresenter, IWorkbenchView}
import scalafx.scene.image.{Image, ImageView, WritableImage}
import scalafxml.core.macros.sfxml

@sfxml
class WorkbenchView(
                   val mainImage: ImageView,
                   val croppedImage: ImageView,
                   val filteredImage: ImageView
                   )
                extends IWorkbenchView {

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

}
