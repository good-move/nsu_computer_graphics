package ru.nsu.fit.g15201.boltava.domain_layer.filter


object DoubleUpscale extends Transformer {

  override def transform(image: RawImage): RawImage = {
    semiBilinear(image)
  }

  private def semiBilinear(image: RawImage): RawImage = {
    val factor = 2
    val width = image.width * factor
    val height = image.height * factor
    val content = for {
      row <- 0 until height
      col <- 0 until width
    } yield {
      val evenRow = row % 2 == 0
      val evenCol = col % 2 == 0
      val sourceIndex = row/2 * image.width + col/2
      val source = image.content(sourceIndex)
      if (evenRow && evenCol) {
        source
      } else if (evenRow && !evenCol) {
        val right = if (col/2 + 1 < image.width) image.content(sourceIndex+1) else source
        UniformScaling.averageFilter(Seq(source, right))
      } else if (!evenRow && evenCol) {
        val bottom = if (row/2 + 1 < image.height) image.content(sourceIndex+image.width) else source
        UniformScaling.averageFilter(Seq(source, bottom))
      } else {
        source
      }
    }

    RawImage(
      width,
      height,
      content.toArray
    )
  }

  private def bilinear(image: RawImage): RawImage = {
    val scaleFactor = 2
    val width = image.width * scaleFactor
    val height = image.height * scaleFactor

    val content = Array.ofDim[Int](width*height)
    for {
      row <- 0 until image.height
      col <- 0 until image.width
    } {
      content(scaleFactor*row*width + scaleFactor*col) = image.content(row*image.width+col)
    }

    for {
      row <- 0 until image.height
      col <- 0 until image.width
    } {
      val current = row*image.width + col
      val rightNeighbor = if (col + 1 < image.width) current+1 else current
      val bottomNeighbor = if (row + 1 < image.height) current + image.width else current
      val rightInterpolated = UniformScaling.averageFilter(Seq(current, rightNeighbor))
      val bottomInterpolated = UniformScaling.averageFilter(Seq(current, bottomNeighbor))
      val scaledRow = scaleFactor * row
      val scaledCol = scaleFactor * col
      content(scaledRow * width + scaledCol + 1) = rightInterpolated
      content((scaledRow + 1) * width + scaledCol) = bottomInterpolated
    }

    for {
      row <- 1 until (height, scaleFactor)
      col <- 1 until (width, scaleFactor)
    } {
      val current = row*width + col
      val top = content(current-width)
      val left = content(current-1)
      val bottom = if (row + 1 < height) content(current+width) else top
      val right = if (col + 1 < width) content(current+1) else left
      val interpolated = UniformScaling.averageFilter(Seq(top, right, bottom, left))
      content(current) = interpolated
    }
    RawImage(
      width,
      height,
      content
    )
  }

}
