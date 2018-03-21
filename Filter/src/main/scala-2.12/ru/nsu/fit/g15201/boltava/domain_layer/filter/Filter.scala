package ru.nsu.fit.g15201.boltava.domain_layer.filter

import ru.nsu.fit.g15201.boltava.domain_layer.color.ColorHelpers

object Filter {

  def applyKernelForIntensity(content: Array[Int], coordinates: Seq[Int], kernel: Array[Int]): Int = {
    kernel.indices.fold(0) { (sum, index) =>
      sum + ColorHelpers.intensity(content(coordinates(index))) * kernel(index)
    }
  }

  def applyKernelWith(content: Array[Int], coordinates: Seq[Int], kernel: Array[Int], fetcher: Int => Int): Int = {
    kernel.indices.fold(0) { (sum, index) =>
      sum + fetcher(content(coordinates(index))) * kernel(index)
    }
  }

  def applyKernelForRgb(content: Array[Int], coordinates: Seq[Int], kernel: Array[Int]): (Int, Int, Int) = {
    kernel.indices.foldLeft((0,0,0))((sum, index) => {
      val color = content(coordinates(index))
      val red = ColorHelpers.red(color)
      val green = ColorHelpers.green(color)
      val blue = ColorHelpers.blue(color)
      val currentKernel = kernel(index)
      (
        sum._1 + red * currentKernel,
        sum._2 + green * currentKernel,
        sum._3 + blue * currentKernel
      )
    })

  }

  @inline
  private def load(content: Array[Int], contentOffset: Int, payload: Array[Int], payloadOffset: Int): Unit = {
    payload(payloadOffset) = content(contentOffset-1)
    payload(payloadOffset+1) = content(contentOffset)
    payload(payloadOffset+2) = content(contentOffset+1)
  }

  def apply3by3Kernel(image: RawImage, sourceConverter: Int=>Int, resultConverter: Int=>Int, kernel: Array[Int]): RawImage = {
    val resultContent = image.content.clone()
    val size = 3
    val neighbors = Array.ofDim[Int](size*size)

    var rowOffset = image.width*(size/2)
    for (_ <- size/2 until image.height-size/2) {
      for (col <- size/2 until image.width-size/2) {
        load(image.content, rowOffset+col-image.width, neighbors, 0)
        load(image.content, rowOffset+col, neighbors, 3)
        load(image.content, rowOffset+col+image.width, neighbors, 6)
        resultContent(rowOffset+col) = resultConverter(neighbors.indices.fold(0) { (sum, index) => {
          sum + sourceConverter(neighbors(index))*kernel(index)
        }})
      }
      rowOffset += image.width
    }

    RawImage(
      image.width,
      image.height,
      resultContent
    )
  }

  def applyColored3by3Kernel(image: RawImage, sourceConverter: Int=>Int, resultConverter: ((Int, Int, Int))=>Int, kernel: Array[Int]): RawImage = {
    val resultContent = image.content.clone()
    val size = 3
    val payload = Array.ofDim[Int](size*size)

    var rowOffset = image.width*(size/2)
    for (_ <- size/2 until image.height-size/2) {
      for (col <- size/2 until image.width-size/2) {
        load(image.content, rowOffset+col-image.width, payload, 0)
        load(image.content, rowOffset+col, payload, 3)
        load(image.content, rowOffset+col+image.width, payload, 6)

        val tempResult = payload.indices.foldLeft((0, 0, 0)) { (groupSum, index) => {
          val kernelValue = kernel(index)
          val colorValue = payload(index)
          val red = sourceConverter(ColorHelpers.red(colorValue) * kernelValue)
          val green = sourceConverter(ColorHelpers.green(colorValue) * kernelValue)
          val blue = sourceConverter(ColorHelpers.blue(colorValue) * kernelValue)

          (
            groupSum._1 + red,
            groupSum._2 + green,
            groupSum._3 + blue
          )
        }}
        resultContent(rowOffset+col) = resultConverter(tempResult)

      }
      rowOffset += image.width
    }

    RawImage(
      image.width,
      image.height,
      resultContent
    )
  }

}
