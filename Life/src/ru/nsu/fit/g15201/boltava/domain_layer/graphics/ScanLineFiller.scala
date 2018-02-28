package ru.nsu.fit.g15201.boltava.domain_layer.graphics

import javafx.scene.paint.Color

import ru.nsu.fit.g15201.boltava.domain_layer.canvas.geometry.Point
import ru.nsu.fit.g15201.boltava.domain_layer.canvas.IDrawable
import ru.nsu.fit.g15201.boltava.domain_layer.logic.Cell

import scala.collection.mutable.ListBuffer

class ScanLineFiller extends IColorFiller {

  override def fillCell(drawable: IDrawable, polygon: Cell, newColor: Color): Unit = {
    val oldColor = drawable.getColor(polygon.getCenter.x, polygon.getCenter.y)

    if (oldColor.equals(newColor)) return

    val stack = new ListBuffer[Point]()
    stack.append(polygon.getCenter)

    while (stack.nonEmpty) {
      val pointToFill = stack.remove(stack.size-1)
      var y = pointToFill.y
      while (y >= 0 && drawable.getColor(pointToFill.x, y) == oldColor) {
        y -= 1
      }
      y += 1
      var filledLeft = false
      var filledRight = false

      while (y < drawable.getHeight && drawable.getColor(pointToFill.x, y) == oldColor) {
        drawable.setColor((pointToFill.x, y), newColor)

        if (!filledLeft && pointToFill.x-1 >= 0 && drawable.getColor(pointToFill.x-1, y) == oldColor) {
          stack.append((pointToFill.x - 1, y))
          filledLeft = true
        } else if (filledLeft && pointToFill.x-1 >= 0 && drawable.getColor(pointToFill.x-1, y) != oldColor) {
          filledLeft = false
        }

        if (!filledRight && pointToFill.x < drawable.getWidth-1 && drawable.getColor(pointToFill.x+1, y) == oldColor) {
          stack.append((pointToFill.x + 1, y))
          filledRight = true
        } else if (filledRight && pointToFill.x+1 < drawable.getWidth && drawable.getColor(pointToFill.x+1, y) != oldColor) {
          filledRight = false
        }

        y+=1
      }

    }
  }

}
