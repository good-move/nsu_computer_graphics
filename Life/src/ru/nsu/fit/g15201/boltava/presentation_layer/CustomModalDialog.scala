package ru.nsu.fit.g15201.boltava.view

import java.net.URL
import javafx.fxml.FXMLLoader
import javafx.scene.control.{ButtonType, Dialog}
import javafx.scene.{Node, Parent, Scene}
import javafx.stage.{Modality, Stage, Window}

abstract class CustomModalDialog[T, R] (
          private val title: String,
          private val cssPath: String,
          private val contentPath: URL,
          private val owner: Window = null
                                ) extends Dialog[R] {


  private var viewController: T = _

  {
    val loader = new FXMLLoader(contentPath)
    val content: Parent = loader.load()
    getDialogPane.setContent(content)
    getDialogPane.getStylesheets.add(cssPath)
    setTitle(title)
    setResizable(false)
    initOwner(owner)
    initModality(Modality.APPLICATION_MODAL)

    viewController = loader.getController[T]()

    makeCloseable()
  }

  private def makeCloseable(): Unit = {
    getDialogPane.getButtonTypes.add(ButtonType.CLOSE)
    val closeButton = getDialogPane.lookupButton(ButtonType.CLOSE)
    closeButton.managedProperty.bind(closeButton.visibleProperty())
    closeButton.setVisible(false)
  }

  def getController: T = {
    viewController
  }

}
