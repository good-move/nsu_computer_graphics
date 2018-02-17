package ru.nsu.fit.g15201.boltava.model.logic

import ru.nsu.fit.g15201.boltava.model.canvas.geometry.{Hexagon, Point}

class HexagonCell(private val center: Point,
                  private val vertices: Array[Point]) extends Hexagon(center, vertices) with Cell {

  var state: State.Value = State.DEAD

  override def getState: State.State = {
    state
  }

  override def setState(state: State.State): Unit = {
    this.state = state
  }

}
