package ru.nsu.fit.g15201.boltava.model.graphics

import javafx.scene.paint.Color

import ru.nsu.fit.g15201.boltava.model.canvas.geometry.Point
import ru.nsu.fit.g15201.boltava.model.canvas.IDrawable
import ru.nsu.fit.g15201.boltava.model.logic.Cell

import scala.collection.mutable.ListBuffer

class ScanLineFiller extends IColorFiller {

  override def fillCell(drawable: IDrawable, polygon: Cell, newColor: Color): Unit = {
    val oldColor = drawable.getColor(polygon.getCenter.x, polygon.getCenter.y)

    if (oldColor.equals(newColor)) return

    val stack = new ListBuffer[Point]()
    stack.append(polygon.getCenter)

    while (stack.nonEmpty) {
      val p = stack.remove(stack.size-1)
      var y = p.y
      while (y >= 0 && drawable.getColor(p.x, y) == oldColor) {
        y -= 1
      }
      y += 1
      var filledLeft = false
      var filledRight = false
      while (y < drawable.getHeight && drawable.getColor(p.x, y) == oldColor) {
        drawable.setColor((p.x, y), newColor)
        if (!filledLeft && p.x-1 >= 0 && drawable.getColor(p.x-1, y) == oldColor) {
          stack.append((p.x - 1, y))
          filledLeft = true
        } else if (filledLeft && p.x-1 >= 0 && drawable.getColor(p.x-1, y) != oldColor) {
          filledLeft = false
        }
        if (!filledRight && p.x < drawable.getWidth-1 && drawable.getColor(p.x+1, y) == oldColor) {
          stack.append((p.x + 1, y))
          filledRight = true
        } else if (filledRight && p.x+1 < drawable.getWidth && drawable.getColor(p.x+1, y) != oldColor) {
          filledRight = false
        }
        y+=1
      }

    }
  }

}
