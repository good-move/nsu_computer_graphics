package ru.nsu.fit.g15201.boltava.presentation_layer.settings

import ru.nsu.fit.g15201.boltava.domain_layer.logic.settings._
import ru.nsu.fit.g15201.boltava.domain_layer.utils.Extensions._
import ru.nsu.fit.g15201.boltava.presentation_layer.settings.IContract.IInteractor


class SettingsPaneInteractor(private val settingsController: ISettingsController) extends IInteractor {

  override def getGameSettings: GameSettings = {
    settingsController.getGameSettings
  }

  override def getSettingsBounds: SettingsBounds = {
    settingsController.getSettingsBounds
  }

  override def applyGameSettings(gameSettings: GameSettings): Unit = {
    validateImpactScores(gameSettings.impactScores) match {
      case Left(message) => throw new InvalidGameSettingsException(message)
      case Right(newScores) => settingsController.applyImpactScores(newScores)
    }

    validateLifeScores(gameSettings.lifeScores) match {
      case Left(message) => throw new InvalidGameSettingsException(message)
      case Right(newScores) => settingsController.applyLifeScores(newScores)
    }

    validatePlaygroundSettings(gameSettings.playgroundSettings, getSettingsBounds) match {
      case Left(message) => throw new InvalidGameSettingsException(message)
      case Right(newSettings) => settingsController.applyPlaygroundSettings(newSettings)
    }
  }

  private def validateImpactScores(settings: ImpactScores): Either[String, ImpactScores] = {
    val firstOrderImpact = settings.firstOrderImpact
    val secondOrderImpact = settings.secondOrderImpact

    if (firstOrderImpact.within(0, Double.MaxValue) && secondOrderImpact.within(0, Double.MaxValue)) {
      Right(new ImpactScores(firstOrderImpact, secondOrderImpact))
    } else {
      Left("Impact score must be a non-negative real value")
    }

  }

  private def validateLifeScores(settings: LifeScores): Either[String, LifeScores] = {
    val minAliveScore = settings.minAliveScore
    val maxAliveScore = settings.maxAliveScore
    val minBirthScore = settings.minBirthScore
    val maxBirthScore = settings.maxBirthScore

    val allNonNegative = Array(minAliveScore, maxAliveScore, minBirthScore, maxBirthScore)
      .forall(score => score.within(0, Double.MaxValue))

    if (!allNonNegative) {
      return Left("All life scores must be non negative real numbers")
    }

    val isBirthScoresOrderValid = minBirthScore <= maxBirthScore
    val isAliveScoresOrderValid = minAliveScore <= maxAliveScore

    val rangeErrorMessageTemplate = "Min %s score cannot be greater than max %s score"

    if (!isBirthScoresOrderValid) {
      return Left(rangeErrorMessageTemplate.format("Birth"))
    }

    if (!isAliveScoresOrderValid) {
      return Left(rangeErrorMessageTemplate.format("Aliveness"))
    }

    if (!(minAliveScore <= minBirthScore && maxBirthScore <= maxAliveScore)) {
      return Left("Birth scores must be between Aliveness scores")
    }

    Right(new LifeScores(
      minAliveScore,
      maxAliveScore,
      minBirthScore,
      maxBirthScore
    ))
  }

  private def validatePlaygroundSettings(settings: PlaygroundSettings, settingsBounds: SettingsBounds): Either[String, PlaygroundSettings] = {
    val isBorderSizeValid = settings.borderSize.within(settingsBounds.minBorderSize, settingsBounds.maxBorderSize)
    if (!isBorderSizeValid) {
      return Left(s"Border size must be in range [${settingsBounds.minBorderSize}, ${settingsBounds.maxBorderSize}]")
    }

    val isBorderWidthValid = settings.borderWidth.within(settingsBounds.minBorderWidth, settingsBounds.maxBorderWidth)
    if (!isBorderWidthValid) {
      return Left(s"Border width must be in range [${settingsBounds.minBorderWidth}, ${settingsBounds.maxBorderWidth}]")
    }

    val isGridDimensionsValid = settings.gridWidth.within(0, settingsBounds.maxGridSize) &&
      settings.gridHeight.within(0, settingsBounds.maxGridSize)
    if (!isGridDimensionsValid) {
      return Left(s"Grid dimensions must be in range [${0}, ${settingsBounds.maxGridSize}]")
    }

    Right(new PlaygroundSettings(
      settings.gridWidth,
      settings.gridHeight,
      settings.borderSize,
      settings.borderWidth
    ))
  }

}


class InvalidGameSettingsException(message: String) extends Exception(message)
