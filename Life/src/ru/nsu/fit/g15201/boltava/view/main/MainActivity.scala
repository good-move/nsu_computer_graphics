package ru.nsu.fit.g15201.boltava.view.main

import javafx.fxml.FXMLLoader
import javafx.scene.paint.Color
import javafx.scene.{Parent, Scene}
import javafx.stage.{Stage, Window}

import ru.nsu.fit.g15201.boltava.model.logic.GameController
import ru.nsu.fit.g15201.boltava.view.base.IBaseActivity
import ru.nsu.fit.g15201.boltava.view.toolbar.{ToolbarInteractor, ToolbarPresenter, ToolbarView}


object MainActivity extends IBaseActivity {

  private val contentPath = getClass.getResource("/ru/nsu/fit/g15201/boltava/view/main/main_view.fxml")

  private var window: Window = _

  private var playgroundPresenter: PlaygroundPresenter = _
  private var toolbarPresenter: ToolbarPresenter = _

  private val gameController = new GameController

  def launch(stage: Stage): Unit = {
    window = stage

    val content: Parent = FXMLLoader.load(contentPath)

    toolbarPresenter = new ToolbarPresenter(ToolbarView.getInstance, new ToolbarInteractor(gameController))
    playgroundPresenter = new PlaygroundPresenter(PlaygroundView.getInstance, new PlaygroundInteractor(gameController))

    val scene = new Scene(content, 800, 500, Color.WHITE)
    scene.getStylesheets.add(getClass.getResource("/styles/buttons.css").toExternalForm)
    stage.setScene(scene)
    stage.show()
  }

  override def getWindow: Window = window

}

