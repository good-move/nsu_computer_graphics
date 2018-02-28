package ru.nsu.fit.g15201.boltava.presentation_layer.toolbar

import ru.nsu.fit.g15201.boltava.domain_layer.canvas.HexagonalGridController
import ru.nsu.fit.g15201.boltava.domain_layer.canvas.geometry.Point
import ru.nsu.fit.g15201.boltava.domain_layer.logic._
import ru.nsu.fit.g15201.boltava.presentation_layer.main.MainActivity
import ru.nsu.fit.g15201.boltava.presentation_layer.settings.SettingsPaneActivity
import ru.nsu.fit.g15201.boltava.presentation_layer.toolbar.IContract.IInteractor

import scala.collection.mutable.ArrayBuffer

class ToolbarInteractor(private val gameController: IGameLogicController) extends IInteractor {

  override def onSaveModel(path: String): Unit = {
    val aliveCells = new ArrayBuffer[Point]()
    gameController.getCells.foreach(_.foreach(cell => {
      if (cell.getState == State.ALIVE) {
        aliveCells.append((cell.getX, cell.getY))
      }
    }))
    ConfigManager.saveGameModel(path, gameController.getGameSettings, aliveCells.toArray)
  }

  override def onOpenModel(path: String): Unit = {
    val gridParameters = ConfigManager.openGameModel(path)
    gameController.setGridController(new HexagonalGridController(gridParameters.borderSize))
    gameController.setGridParams(gridParameters)
  }

  override def onSetReplace(): Unit = {
    gameController.setCellSelectionMode(CellSelectionMode.REPLACE)
  }

  override def onSetToggle(): Unit = {
    gameController.setCellSelectionMode(CellSelectionMode.TOGGLE)
  }

  override def onOpenSettings(): Unit = {
    // todo: fix weird stuff (view inside model)!
    SettingsPaneActivity.launch(MainActivity.getWindow, gameController)
  }

  override def getGameController: IGameLogicController = gameController

}

