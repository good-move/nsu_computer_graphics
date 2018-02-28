package ru.nsu.fit.g15201.boltava.model.logic

class GameSettings {

  private var _gridWidth: Int = 0
  private var _gridHeight: Int = 0
  private var _borderWidth: Int = 0
  private var _borderSize: Int = 0
  private var _aliveCells: Array[(Int, Int)] = _
  private var _minAliveScore: Double = 0
  private var _maxBirthScore: Double = 0
  private var _maxAliveScore: Double = 0
  private var _minBirthScore: Double = 0


  def minAliveScore: Double = _minAliveScore

  def minAliveScore_=(value: Double): Unit = {
    _minAliveScore = value
  }

  def maxAliveScore: Double = _maxAliveScore

  def maxAliveScore_=(value: Double): Unit = {
    _maxAliveScore = value
  }

  def minBirthScore: Double = _minBirthScore

  def minBirthScore_=(value: Double): Unit = {
    _minBirthScore = value
  }


  def maxBirthScore: Double = _maxBirthScore

  def maxBirthScore_=(value: Double): Unit = {
    _maxBirthScore = value
  }

  def aliveCells: Array[(Int, Int)] = _aliveCells

  def aliveCells_=(value: Array[(Int, Int)]): Unit = {
    _aliveCells = value
  }

  def borderSize: Int = _borderSize

  def borderSize_=(value: Int): Unit = {
    _borderSize = value
  }

  def width: Int = _gridWidth

  def width_=(value: Int): Unit = {
    _gridWidth = value
  }


  def height: Int = _gridHeight

  def height_=(value: Int): Unit = {
    _gridHeight = value
  }


  def borderWidth: Int = _borderWidth

  def borderWidth_=(value: Int): Unit = {
    _borderWidth = value
  }

}

object GameSettings {

  def apply(other: GameSettings): GameSettings = {
    val gridSettings = new GameSettings
    gridSettings.borderSize = other.borderSize
    gridSettings.borderWidth = other.borderWidth
    gridSettings.aliveCells = other.aliveCells
    gridSettings.width = other.width
    gridSettings.height = other.height
    gridSettings
  }

}
