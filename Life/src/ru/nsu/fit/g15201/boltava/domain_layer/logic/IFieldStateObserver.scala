package ru.nsu.fit.g15201.boltava.domain_layer.logic


trait IFieldStateObserver {
  def onFieldUpdated(nextField: Array[Array[Cell]])
}
