package ru.nsu.fit.g15201.boltava.domain_layer.filter

import ru.nsu.fit.g15201.boltava.domain_layer.color.ColorHelpers
import ru.nsu.fit.g15201.boltava.domain_layer.geometry.Dimensions

import scala.annotation.tailrec


object UniformDownscale {

  def apply(dimensions: Dimensions): Transformer = new Downscale(dimensions)

}


sealed class Downscale(val dimensions: Dimensions) extends Transformer {

  private val log10_2 = Math.log10(2.0)

  private def log2(x: Double) = Math.log10(x) / log10_2

  override def transform(image: RawImage): RawImage = {
    val scale = (image.width.toDouble / dimensions.width).max(image.height.toDouble/dimensions.height)
    downscale(image, scale)
  }

  def downscale(image: RawImage, factor: Double): RawImage = {
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

      averageFilter(sampleSource)
    }

    RawImage(
      width,
      height,
      sampledImage.toArray
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

    averageFilter(sampleSource)
  }

  private def averageFilter(argb: Seq[Int]): Int = {
    val length = argb.length
    val summer = (fetcher: Int => Int) => (sum: Int, current: Int) => sum + fetcher(current)
    val alpha = argb.fold(0)(summer(ColorHelpers.alpha)) / length
    val red = argb.fold(0)(summer(ColorHelpers.red)) / length
    val green = argb.fold(0)(summer(ColorHelpers.green)) / length
    val blue = argb.fold(0)(summer(ColorHelpers.blue)) / length

    ColorHelpers.intArgb(alpha, red, green, blue)
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
      averageFilter(Seq(
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

  private def downscaleWithAllPixels(image: RawImage, factor: Double): RawImage = {
    val width = (image.width / factor).toInt
    val height = (image.height / factor).toInt
    val step = factor.toInt
    val widthRemainder = image.width % (width*step)
    val heightRemainder = image.height % (height*step)

    val content = Array.ofDim[Int](width * height)

    val columnsProps = Array.ofDim[(Int, Int)](width)
    columnsProps(0) = (0, step + {if (0 < widthRemainder) 1 else 0})
    for (i <- 1 until width) {
      columnsProps(i) = (columnsProps(i-1)._1 + columnsProps(i-1)._2, step + {if (i < widthRemainder) 1 else 0})
    }

    val rowsProps = Array.ofDim[(Int, Int)](height)
    rowsProps(0) = (0, step + {if (0 < heightRemainder) 1 else 0})
    for (i <- 1 until height) {
      rowsProps(i) = (rowsProps(i-1)._1 + rowsProps(i-1)._2, step + {if (i < heightRemainder) 1 else 0})
    }
    println(columnsProps)
    println(rowsProps)

    for {
      row <- 0 until height
      col <- 0 until width
    } content(row*width + col) = samplePixel(image, rowsProps(row), columnsProps(col) )

    RawImage(
      width,
      height,
      content
    )
  }




}


