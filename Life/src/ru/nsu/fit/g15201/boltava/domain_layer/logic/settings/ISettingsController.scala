package ru.nsu.fit.g15201.boltava.domain_layer.logic.settings

trait ISettingsController {

  def applyImpactScores(impactScores: ImpactScores)
  def applyLifeScores(lifeScores: LifeScores)
  def applyPlaygroundSettings(playgroundSettings: PlaygroundSettings)

  def getGameSettings: GameSettings
  def getSettingsBounds: SettingsBounds

}
