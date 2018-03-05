package ru.nsu.fit.g15201.boltava.domain_layer.logic.settings

import ru.nsu.fit.g15201.boltava.domain_layer.logic.GridType._
import ru.nsu.fit.g15201.boltava.domain_layer.logic.GridType.GridType

case class PlaygroundSettings(
      var gridWidth: Int = 0,
      var gridHeight: Int = 0,
      var borderSize: Int = 0,
      var borderWidth: Int = 0,
      var aliveCells: Array[(Int, Int)] = null,
      var gridType: GridType = HEXAGON
)