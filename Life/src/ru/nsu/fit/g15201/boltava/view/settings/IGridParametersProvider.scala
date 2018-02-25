package ru.nsu.fit.g15201.boltava.view.settings

import ru.nsu.fit.g15201.boltava.model.logic.GridSettings

trait IGridParametersProvider {
  def setChangeListener(gridParametersChangeListener: IGridParametersChangeListener)
  def removeChangeListener()
}
