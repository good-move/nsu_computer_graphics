import data_layer.settings.Config


object Wireframe extends App {
  val config = {
    import data_layer.settings.ConfigurationReader._
    pureconfig.loadConfigOrThrow[Config]
  }
  for (solid <- config.solids) {
    println(solid.pivot)
  }

}
