import breeze.linalg.{DenseMatrix, DenseVector}
import data_layer.settings.Config
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import scalafxml.core.{FXMLLoader, NoDependencyResolver}
import scalafx.Includes._

object Wireframe extends JFXApp {

  val config = {
    import data_layer.settings.ConfigurationReader._
    pureconfig.loadConfigOrThrow[Config]
  }

  private val root = new FXMLLoader(getClass.getResource("window.fxml"), NoDependencyResolver).load[javafx.scene.Parent]

  stage = new PrimaryStage {
    title = "WireFrame"
    scene = new Scene(root)
  }

}
