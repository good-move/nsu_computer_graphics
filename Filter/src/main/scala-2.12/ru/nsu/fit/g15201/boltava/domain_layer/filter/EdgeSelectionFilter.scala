package ru.nsu.fit.g15201.boltava.domain_layer.filter

import ru.nsu.fit.g15201.boltava.domain_layer.color.ColorHelpers
import ru.nsu.fit.g15201.boltava.domain_layer.filter.edge_detection.EdgeDetectionKernel

object EdgeSelectionFilter extends {

  def apply(kernel: EdgeDetectionKernel): Transformer = new EdgeSelectionFilter(kernel)

}


final class EdgeSelectionFilter(kernel: EdgeDetectionKernel) extends Transformer {

  override def transform(image: RawImage): RawImage = {
    if (kernel.xGradient.length == 4) {
      handleRobertKernel(image)
    } else {
      handleStandardKernel(image)
    }
  }

  private def thresholdColor(gradient: Int, thresholdSquare: Double): Int = {
    if (gradient > thresholdSquare) ColorHelpers.getWhite
    else ColorHelpers.getBlack
  }

  private def handleRobertKernel(image: RawImage): RawImage = {
    val width = image.width
    val height = image.height
    val thresholdSquare = Math.pow(kernel.threshold, 2)

    val filteredContent = for {
      row <- 0 until height
      col <- 0 until width
    } yield {
      val offset = row * width + col
      val rowSign = if (row + 1 != height) 1 else -1
      val colSign = if (col + 1 != width) 1 else -1

      val coordinates = Seq(
        offset,
        offset + colSign,
        offset + rowSign * width,
        offset + rowSign * width + colSign
      )

      val xGradient = applyKernel(image.content, coordinates, kernel.xGradient)
      val yGradient = applyKernel(image.content, coordinates, kernel.yGradient)

      val gradient = xGradient*xGradient + yGradient * yGradient

      thresholdColor(gradient, thresholdSquare)
    }

    RawImage(
      width,
      height,
      filteredContent.toArray
    )
  }

  private def handleStandardKernel(image: RawImage): RawImage = {
    val width = image.width
    val height = image.height
    val thresholdSquare = Math.pow(kernel.threshold, 2)

    val filteredContent = for {
      row <- 0 until height
      col <- 0 until width
    } yield {
      val currentCell = row * width + col

      val prevRow = if (row != 0) -1 else 1
      val nextRow = if (row + 1 != height) width else -width
      val prevCol = if (col != 0) -1 else 1
      val nextCol = if (col + 1 != width) 1 else -1

      val coordinates = Seq(
        currentCell + prevCol + prevRow,
        currentCell + prevRow,
        currentCell + nextCol + prevRow,
        currentCell + prevCol,
        currentCell,
        currentCell + nextCol,
        currentCell + prevCol + nextRow,
        currentCell + nextRow,
        currentCell + nextCol + nextRow
      )

      val xGradient = applyKernel(image.content, coordinates, kernel.xGradient)
      val yGradient = applyKernel(image.content, coordinates, kernel.yGradient)

      val gradient = xGradient*xGradient + yGradient * yGradient

      thresholdColor(gradient, thresholdSquare)
    }

    RawImage(
      width,
      height,
      filteredContent.toArray
    )
  }

  private def applyKernel(content: Array[Int], coordinates: Seq[Int], kernel: Array[Int]): Int = {
    kernel.indices.fold(0) { (sum, index) =>
      sum + ColorHelpers.intensity(content(coordinates(index))) * kernel(index)
    }
  }

}
