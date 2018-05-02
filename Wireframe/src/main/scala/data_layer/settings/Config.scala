package data_layer.settings

import data_layer.geometry.{Domain, WireFrame}
import data_layer.graphics.Color

case class Config(background: Color,
                  wireframes: Seq[WireFrame]
                 )
