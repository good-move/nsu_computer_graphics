package ru.nsu.fit.g15201.boltava.domain_layer.logic.settings

class ImpactScores(var firstOrderImpact: Double = 0, var secondOrderImpact: Double = 0) {
  def copy():ImpactScores = {
    new ImpactScores(
      firstOrderImpact,
      secondOrderImpact
    )
  }
}

object ImpactScores {

  def apply(gameSettings: GameSettings): ImpactScores = new ImpactScores()

}