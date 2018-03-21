package ru.nsu.fit.g15201.boltava.domain_layer.filter

import ru.nsu.fit.g15201.boltava.domain_layer.geometry.IntDimensions
import ru.nsu.fit.g15201.boltava.domain_layer.util.Utils

import scala.annotation.tailrec


object UniformDownscale {

  def apply(dimensions: IntDimensions): Transformer = new Downscale(dimensions)

}


sealed class Downscale(val dimensions: IntDimensions) extends Transformer {

  private val log10_2 = Math.log10(2.0)

  private def log2(x: Double) = Math.log10(x) / log10_2

  override def transform(image: RawImage): RawImage = {
    val scale = (image.width.toDouble / dimensions.width).max(image.height.toDouble/dimensions.height)
    if (scale > 1) {
//      Utils.withTime {downscale(image, scale)}
//      Utils.withTime {fastDownscale(image, scale)}
      fastDownscale(image, scale)
    } else {
      image
    }
  }

  private def downscale(image: RawImage, factor: Double): RawImage = {
    val content = image.content
    val width = (image.width / factor).toInt
    val height = (image.height / factor).toInt
    val sampleSize = factor.toInt

    val sampledImage = for {
      row <- 0 until height
      col <- 0 until width
    } yield {
      val initialOffset = (factor*row).toInt * image.width + (col*factor).toInt

      val sampleSource = for {
        rowOffset <- 0 until sampleSize
        colOffset <- 0 until sampleSize
      } yield content(initialOffset + rowOffset*image.width + colOffset)

      UniformScaling.averageFilter(sampleSource)
    }

    RawImage(
      width,
      height,
      sampledImage.toArray
    )
  }

  private def fastDownscale(image: RawImage, factor: Double): RawImage = {
    val content = image.content
    val width = (image.width / factor).toInt
    val height = (image.height / factor).toInt
    val sampleSize = factor.toInt

    val sampledImage = Array.ofDim[Int](width*height)
    val sampleSource = Array.ofDim[Int](sampleSize*sampleSize)

    var currentDestinationRow = 0
    var currentSourceRow = 0.0
    for (_ <- 0 until height) {
      var currentCol = 0.0
      for (col <- 0 until width) {
        val offset = currentSourceRow.toInt * image.width + currentCol.toInt

        var sampleSourceOffset = 0
        var rowOffset = 0
        for (_ <- 0 until sampleSize) {
          for (colOffset <- 0 until sampleSize) {
            sampleSource(sampleSourceOffset) = content(offset + rowOffset + colOffset)
            sampleSourceOffset += 1
          }
          rowOffset += image.width
        }

        sampledImage(currentDestinationRow + col) = UniformScaling.averageFilter(sampleSource)

        currentCol += factor
      }
      currentSourceRow += factor
      currentDestinationRow += width
    }

    RawImage(
      width,
      height,
      sampledImage
    )
  }

  type SampleProps = (Int, Int) // offset, length

  private def samplePixel(source: RawImage, rowProp: SampleProps, colProp: SampleProps): Int = {
    val sourceWidth = source.width
    val initialRow = rowProp._1
    val initialCol = colProp._1

    val sampleSource = for {
      rowOffset <- 0 until rowProp._2
      colOffset <- 0 until colProp._2
    } yield source.content((initialRow + rowOffset)*sourceWidth + initialCol + colOffset)

    UniformScaling.averageFilter(sampleSource)
  }

  private def halfDownscale(image: RawImage): RawImage = {
    val content = image.content
    val nextWidth = image.width / 2
    val nextHeight = image.height / 2

    val scaledContent = for {
      row <- 0 until (image.height, 2)
      col <- 0 until (image.width, 2)
    } yield {
      val mappedIndex = row * image.width + col
      UniformScaling.averageFilter(Seq(
        content(mappedIndex),
        content(mappedIndex + 1),
        content(mappedIndex + image.width),
        content(mappedIndex + image.width + 1)
      ))
    }
    RawImage(
      nextWidth,
      nextHeight,
      scaledContent.toArray
    )
  }

  @tailrec
  private def pyramidDownscale(image: RawImage, iterations: Int): RawImage = {
    if (iterations < 1) image
    else pyramidDownscale(halfDownscale(image), iterations-1)
  }

}


