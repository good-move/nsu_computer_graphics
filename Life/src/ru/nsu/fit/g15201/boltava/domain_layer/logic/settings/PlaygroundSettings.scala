package ru.nsu.fit.g15201.boltava.domain_layer.logic.settings

class PlaygroundSettings(
      var gridWidth: Int = 0,
      var gridHeight: Int = 0,
      var borderSize: Int = 0,
      var borderWidth: Int = 0,
      var aliveCells: Array[(Int, Int)] = null
) {

  def copy(): PlaygroundSettings = {
    new PlaygroundSettings(
      gridWidth,
      gridHeight,
      borderSize,
      borderWidth,
      aliveCells
    )
  }

}