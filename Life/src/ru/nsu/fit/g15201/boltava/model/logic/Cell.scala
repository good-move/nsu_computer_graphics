package ru.nsu.fit.g15201.boltava.model.logic

import ru.nsu.fit.g15201.boltava.model.canvas.geometry.Point

trait Cell {

  def getCenter: Point
  def getVertices: Array[Point]

  def getState: State.State
  def setState(state: State.State): Unit

}

object State extends Enumeration {
  type State = Value
  val ALIVE, DEAD = Value
}
