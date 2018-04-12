package data_layer.settings

import data_layer.geometry.{Domain, Solid}
import data_layer.graphics.Color

case class Config(domain: Domain,
                  background: Color,
                  solids: Seq[Solid]
                 )
