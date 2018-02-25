package ru.nsu.fit.g15201.boltava.model.logic

class GridSettings {

  private[this] var _width: Int = 0
  private[this] var _height: Int = 0
  private[this] var _borderWidth: Int = 0
  private[this] var _cellSideSize: Int = 0
  private[this] var _aliveCells: Array[(Int, Int)] = _

  def aliveCells: Array[(Int, Int)] = _aliveCells

  def aliveCells_=(value: Array[(Int, Int)]): Unit = {
    _aliveCells = value
  }

  def cellSideSize: Int = _cellSideSize

  def cellSideSize_=(value: Int): Unit = {
    _cellSideSize = value
  }

  def width: Int = _width

  def width_=(value: Int): Unit = {
    _width = value
  }


  def height: Int = _height

  def height_=(value: Int): Unit = {
    _height = value
  }


  def borderWidth: Int = _borderWidth

  def borderWidth_=(value: Int): Unit = {
    _borderWidth = value
  }

}

object GridSettings {

  def apply(other: GridSettings): GridSettings = {
    val gridSettings = new GridSettings
    gridSettings.cellSideSize = other.cellSideSize
    gridSettings.borderWidth = other.borderWidth
    gridSettings.aliveCells = other.aliveCells
    gridSettings.width = other.width
    gridSettings.height = other.height
    gridSettings
  }

}
