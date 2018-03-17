package ru.nsu.fit.g15201.boltava.presentation_layer.workbench

import ru.nsu.fit.g15201.boltava.domain_layer.geometry.DoublePoint
import ru.nsu.fit.g15201.boltava.domain_layer.filter.RawImage
import ru.nsu.fit.g15201.boltava.presentation_layer.base.{IBasePresenter, IBaseView}

import scalafx.scene.image.Image

object Contract {

  trait IWorkbenchPresenter extends IBasePresenter {

    def onImagePartSelected(topLeft: DoublePoint, bottomRight: DoublePoint)
    def onImageSelectionMoved(topLeft: DoublePoint, bottomRight: DoublePoint)

  }

  trait IWorkbenchView extends IBaseView[IWorkbenchPresenter] {

    def setMainImage(image: Image)
    def setCroppedImage(transformableImage: RawImage)
    def setFilteredImage(transformableImage: RawImage)

  }

}
