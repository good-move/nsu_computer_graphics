package ru.nsu.fit.g15201.boltava.domain_layer.logic.settings

import ru.nsu.fit.g15201.boltava.domain_layer.primitives.Color

case class Settings(xNodes: Int,
                    yNodes: Int,
                    levels: Int,
                    isolineColor: Color,
                    legendColors: Seq[Color]
                   )
