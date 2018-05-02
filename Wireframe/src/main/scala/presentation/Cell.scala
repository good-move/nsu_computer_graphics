package presentation

import javafx.scene.control.ListCell
import scalafx.scene.control.{Button, CheckBox, Label}
import scalafx.scene.layout.GridPane

class Cell extends ListCell[Layer] {

  private val gridPane = new GridPane()

  private val label = new Label()
  val removeButton = new Button("-")
  val checkBox = new CheckBox()

  {
    gridPane.add(checkBox, 0, 0)
    gridPane.add(removeButton, 0, 1)
    gridPane.add(label, 1, 1, 2, 1)
  }

  def onSelect(): Unit = {
    checkBox.selected = true
  }

  override def updateItem(layer: Layer, empty: Boolean): Unit = {
    super.updateItem(layer, empty)
    if (empty) {
      setGraphic(null)
    } else {
      this.label.text = layer.label
      this.checkBox.selected = layer.visible
      setGraphic(gridPane)
    }
  }

}
