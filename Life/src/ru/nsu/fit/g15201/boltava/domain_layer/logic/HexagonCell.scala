package ru.nsu.fit.g15201.boltava.domain_layer.logic

import ru.nsu.fit.g15201.boltava.domain_layer.canvas.geometry.Point

class HexagonCell(private val center: Point,
                  private val vertices: Array[Point],
                  private val x: Int, private val y: Int,
                  private var impact: Double = 0
                 ) extends Cell {

  var state: State.State = State.DEAD

  override def getState: State.State = state

  override def setState(state: State.State): Unit = {
    this.state = state
  }

  override def getCenter: Point = center

  override def getVertices: Array[Point] = vertices

  override def getX: Int = x

  override def getY: Int = y

  override def getImpact: Double = impact

  override def setImpact(impact: Double): Unit = this.impact = impact
}
