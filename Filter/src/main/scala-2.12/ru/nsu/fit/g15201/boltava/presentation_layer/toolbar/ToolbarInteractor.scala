package ru.nsu.fit.g15201.boltava.presentation_layer.toolbar

import ru.nsu.fit.g15201.boltava.domain_layer.settings.{FileExtension, ImageProperties}
import ru.nsu.fit.g15201.boltava.presentation_layer.toolbar.Contract.IToolbarInteractor

class ToolbarInteractor extends IToolbarInteractor {

  override def getValidImageExtensions: Seq[FileExtension] = ImageProperties.allowedExtensions

}

