package ru.nsu.fit.g15201.boltava.domain_layer.settings

object ImageProperties {

  val allowedExtensions = Seq(
    FileExtension("bmp"),
    FileExtension("png")
  )

  val allowedDepth = ImageDepth(24)

}


case class FileExtension(extension: String) extends AnyVal
case class ImageDepth(depth: Int) extends AnyVal