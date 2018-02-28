package ru.nsu.fit.g15201.boltava.domain_layer.logic.settings

class GameSettings(
  var playgroundSettings: PlaygroundSettings = new PlaygroundSettings(),
  var lifeScores: LifeScores = new LifeScores(),
  var impactScores: ImpactScores = new ImpactScores()
) {

  def copy(): GameSettings = {
    new GameSettings(
      playgroundSettings.copy(),
      lifeScores.copy(),
      impactScores.copy()
    )
  }
}


object GameSettings {

  val MIN_ALIVE_SCORE = 2.0
  val MAX_ALIVE_SCORE = 3.3

  val MIN_BIRTH_SCORE = 2.3
  val MAX_BIRTH_SCORE = 2.9

  val FIRST_ORDER_IMPACT = 1.0
  val SECOND_ORDER_IMPACT = .3

  val MAX_GRID_SIZE = 500

  val MIN_BORDER_SIZE = 5
  val MAX_BORDER_SIZE = 50

  val MIN_BORDER_WIDTH = 1
  val MAX_BORDER_WIDTH = 15

}