package ru.nsu.fit.g15201.boltava.model.logic

trait Cell {

  def getState: State.State
  def setState(state: State.State): Unit

  object State extends Enumeration {
    type State = Value
    val ALIVE, DEAD = Value
  }

}
