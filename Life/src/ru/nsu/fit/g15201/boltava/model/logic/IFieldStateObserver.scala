package ru.nsu.fit.g15201.boltava.model.logic


trait IFieldStateObserver {
  def onFieldUpdated(nextField: Array[Array[Cell]])
}
