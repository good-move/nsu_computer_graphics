package ru.nsu.fit.g15201.boltava.model.graphics

import javafx.scene.paint.Color

import ru.nsu.fit.g15201.boltava.model.canvas.{IDrawable, Point}
import ru.nsu.fit.g15201.boltava.model.logic.Hexagon

import scala.collection.mutable

class ScanLineFiller extends IColorFiller[Hexagon] {

  override def fillCell(drawable: IDrawable, hex: Hexagon, newColor: Color): Unit = {
    val oldColor = drawable.getColor(hex.center.x, hex.center.y)

    if (oldColor.equals(newColor)) return

    val stack = new mutable.Stack[Point]()
    stack.push(hex.center)

    while (stack.nonEmpty) {
      val p = stack.pop()
      var y = p.y
      while (y >= 0 && drawable.getColor(p.x, y) == oldColor) {
        y -= 1
      }
      y += 1
      var spanLeft = false
      var spanRight = false
      while (y < drawable.getHeight && drawable.getColor(p.x, y) == oldColor) {
        drawable.setColor((p.x, y), newColor)
        if (!spanLeft && p.x-1 >= 0 && drawable.getColor(p.x-1, y) == oldColor) {
          stack.push((p.x - 1, y))
          spanLeft = false
        } else if (spanLeft && p.x-1 >= 0 && drawable.getColor(p.x-1, y) != oldColor) {
          spanLeft = false
        }
        if (!spanRight && p.x < drawable.getWidth-1 && drawable.getColor(p.x+1, y) == oldColor) {
          stack.push((p.x + 1, y))
          spanRight = false
        } else if (spanRight && p.x+1 < drawable.getWidth && drawable.getColor(p.x+1, y) != oldColor) {
          spanRight = false
        }
        y+=1
      }

    }
  }

}
