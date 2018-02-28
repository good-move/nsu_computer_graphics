package ru.nsu.fit.g15201.boltava.domain_layer.graphics

import javafx.scene.paint.Color

import ru.nsu.fit.g15201.boltava.domain_layer.canvas.IDrawable
import ru.nsu.fit.g15201.boltava.domain_layer.logic.Cell

/**
  * Interface to implement a cell filling algorithm
  */
trait IColorFiller {
  def fillCell(drawable: IDrawable, cell: Cell, color: Color)
}
