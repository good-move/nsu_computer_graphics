package ru.nsu.fit.g15201.boltava.domain_layer.logic

import ru.nsu.fit.g15201.boltava.domain_layer.canvas.geometry.Point

trait Cell {

  def getCenter: Point
  def getVertices: Array[Point]

  def getX: Int
  def getY: Int

  def getImpact: Double
  def setImpact(impact: Double)

  def getState: State.State
  def setState(state: State.State): Unit

}

object State extends Enumeration {
  type State = Value
  val ALIVE, DEAD = Value
}
