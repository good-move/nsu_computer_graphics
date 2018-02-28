package ru.nsu.fit.g15201.boltava.domain_layer.logic.settings

class GameSettings (
  var gridWidth: Int = 0,
  var gridHeight: Int = 0,
  var borderWidth: Int = 0,
  var borderSize: Int = 0,
  var aliveCells: Array[(Int, Int)] = null,
  var minAliveScore: Double = 0,
  var maxAliveScore: Double = 0,
  var minBirthScore: Double = 0,
  var maxBirthScore: Double = 0,
  var firstOrderImpact: Double = 0,
  var secondOrderImpact: Double = 0
) {

  def copy(
    gridWidth: Int = this.gridWidth,
    gridHeight: Int = this.gridHeight,
    borderWidth: Int = this.borderWidth,
    borderSize: Int = this.borderSize,
    aliveCells: Array[(Int, Int)] = this.aliveCells,
    minAliveScore: Double = this.minAliveScore,
    maxAliveScore: Double = this.maxAliveScore,
    minBirthScore: Double = this.minBirthScore,
    maxBirthScore: Double = this.maxBirthScore,
    firstOrderImpact: Double = this.firstOrderImpact,
    secondOrderImpact: Double = this.secondOrderImpact
  ): GameSettings = {
    new GameSettings(
      gridWidth,
      gridHeight,
      borderWidth,
      borderSize,
      aliveCells,
      minAliveScore,
      maxAliveScore,
      minBirthScore,
      maxBirthScore,
      firstOrderImpact,
      secondOrderImpact
    )

  }

}
