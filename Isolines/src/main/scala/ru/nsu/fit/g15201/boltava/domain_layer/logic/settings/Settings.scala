package ru.nsu.fit.g15201.boltava.domain_layer.logic.settings

import ru.nsu.fit.g15201.boltava.domain_layer.primitives.Color

case class Settings(xWidth: Int,
                    yWidth: Int,
                    levels: Int,
                    isolineColor: Color,
                    legendColors: Iterable[Color]
                   )
