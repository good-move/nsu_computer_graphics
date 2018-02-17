package ru.nsu.fit.g15201.boltava.model.logic

trait Cell {

  object State extends Enumeration {
    val ALIVE = Value(0)
    val DEAD = Value(1)
  }

}
