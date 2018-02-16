package ru.nsu.fit.g15201.boltava.model.graphics

import ru.nsu.fit.g15201.boltava.model.logic.Cell


/**
  * This class is intended to control various grid systems.
  *
  * Creates arbitrary cell space with its own coordinate system
  * @tparam CellType
  */
trait IGridController[CellType <: Cell] {

  def generateGrid(width: Int, height: Int): Array[Array[CellType]]

  /**
    * Get cartesian coordinates for cell with coordinates `point`,
    * given in terms of this space
    * @param point point to convert to Cartesian
    * @return new point, which represents the passed point in Cartesian coordinates
    */
  def getHexCenter(point: Point): DoublePoint

}
