package ru.nsu.fit.g15201.boltava.domain_layer.logic.settings

class LifeScores(
  var minAliveScore: Double = 0,
  var maxAliveScore: Double = 0,
  var minBirthScore: Double = 0,
  var maxBirthScore: Double = 0
) {

  def copy(): LifeScores = {
    new LifeScores(
      minAliveScore,
      maxAliveScore,
      minBirthScore,
      maxBirthScore
    )
  }

}
