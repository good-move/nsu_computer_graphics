package ru.nsu.fit.g15201.boltava.domain_layer.filter

import ru.nsu.fit.g15201.boltava.domain_layer.color.ColorHelpers

class MedianFilter(neighborsGridSize: Int) extends Transformer {

  @inline
  private def load(content: Array[Int], contentOffset: Int, payload: Array[Int], payloadOffset: Int, rowSize: Int): Unit = {
    for (i <- 0 until (rowSize-1,2)) {
      payload(payloadOffset+i) = content(contentOffset+i)
      payload(payloadOffset+i+1) = content(contentOffset+i+1)
    }
    payload(payloadOffset+rowSize-1) = content(contentOffset+rowSize-1)
  }

  override def transform(image: RawImage): RawImage = {
    val content = image.content.clone()
    val size = neighborsGridSize
    val medianIndex = size*size / 2
    val payload = Array.ofDim[Int](size*size)
    val borderOffset = size / 2
    val initialContentOffset = -image.width*borderOffset
    var rowOffset = -initialContentOffset

    for (_ <- borderOffset until image.height - borderOffset) {
      for (col <- borderOffset until image.width - borderOffset) {
        var payloadOffset = 0
        var batchOffset = initialContentOffset
        for (_ <- 0 until (size, 1)) {
          load(image.content, rowOffset+col+batchOffset-borderOffset, payload, payloadOffset, size)
          payloadOffset += size
          batchOffset += image.width
        }
        content(rowOffset+col) = payload.sortBy(ColorHelpers.intensity)(Ordering.Int)(medianIndex)
      }
      rowOffset += image.width
    }

    RawImage(
      image.width,
      image.height,
      content
    )
  }

  def withTime[R](body: => R): R = {
    val startTime = System.nanoTime()
    val result = body
    val endTime = System.nanoTime()
    println(s"Execution time: ${endTime - startTime}ns")
    result
  }

}


object MedianFilter {

  def apply(neighborsGridSize: Int): MedianFilter = new MedianFilter(neighborsGridSize)

}