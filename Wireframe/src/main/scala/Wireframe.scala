import breeze.linalg.{DenseMatrix, DenseVector}
import data_layer.settings.Config
import presentation.IPresenter
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import scalafxml.core.{DependenciesByType, FXMLLoader, NoDependencyResolver}
import scalafx.Includes._

object Wireframe extends JFXApp {

  val config = {
    import data_layer.settings.ConfigurationReader._
    pureconfig.loadConfigOrThrow[Config]
  }

  private val loader = new FXMLLoader(
    getClass.getResource("window.fxml"),
    NoDependencyResolver
  )
  stage = new PrimaryStage {
    title = "WireFrame"
    scene = new Scene(loader.load[javafx.scene.Parent])
  }

  loader.getController[IPresenter].setScene(stage.scene.value)
}
