package ru.nsu.fit.g15201.boltava.model.canvas
import javafx.scene.image.WritableImage
import javafx.scene.paint.Color

class ImageDrawable(private val writableImage: WritableImage) extends IDrawable {

  override def setColor(point: Point, color: Color): Unit = {
    writableImage.getPixelWriter.setColor(point.x, point.y, color)
  }

  override def getColor(point: Point): Color = {
    writableImage.getPixelReader.getColor(point.x, point.y)
  }

  override def draw(points: Array[Point], color: Color = Color.BLACK): Unit = {
    for (p <- points) {
      setColor(p, color)
    }
  }

  override def getHeight: Double = writableImage.getHeight

  override def getWidth: Double = writableImage.getWidth
}
