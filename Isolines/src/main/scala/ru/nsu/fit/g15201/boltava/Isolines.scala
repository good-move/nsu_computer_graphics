package ru.nsu.fit.g15201.boltava

import ru.nsu.fit.g15201.boltava.domain_layer.controllers.MainController
import ru.nsu.fit.g15201.boltava.presentation_layer.menu.MenuComponent
import ru.nsu.fit.g15201.boltava.presentation_layer.toolbar.ToolbarComponent
import ru.nsu.fit.g15201.boltava.presentation_layer.workbench.WorkbenchComponent
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import scalafx.scene.layout.{Priority, VBox}


object Isolines extends JFXApp {

  stage = new PrimaryStage
  implicit val implicitStage = stage

  val mainController = new MainController

  val menuComponent = MenuComponent(mainController.menuInteractor)
  val toolbarComponent = ToolbarComponent(mainController.menuInteractor, mainController.settingsInteractor)
  val workbenchComponent = WorkbenchComponent(mainController.workbenchInteractor)

  val menu = menuComponent.root
  menu.hgrow = Priority.Always

  val toolbar = toolbarComponent.root
  toolbar.hgrow = Priority.Always

  val workbench = workbenchComponent.root
  workbench.vgrow = Priority.Always

  val wrapper = new VBox(
    menu,
    toolbar,
    workbench
  )

  val scene = new Scene(wrapper)
  stage.minHeight = 500
  stage.minWidth = 735

  stage.scene = scene
  stage.show()
}

