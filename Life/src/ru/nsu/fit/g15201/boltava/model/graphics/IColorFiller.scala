package ru.nsu.fit.g15201.boltava.model.graphics

import javafx.scene.paint.Color

import ru.nsu.fit.g15201.boltava.model.canvas.IDrawable
import ru.nsu.fit.g15201.boltava.model.logic.Cell

/**
  * Interface to implement a cell filling algorithm
  */
trait IColorFiller[T <: Cell] {
  def fillCell(drawable: IDrawable, cell: T, color: Color)
}
