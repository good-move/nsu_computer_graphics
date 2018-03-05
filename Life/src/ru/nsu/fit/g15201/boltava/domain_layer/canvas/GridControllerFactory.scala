package ru.nsu.fit.g15201.boltava.domain_layer.canvas

import ru.nsu.fit.g15201.boltava.domain_layer.logic.GridType.GridType
import ru.nsu.fit.g15201.boltava.domain_layer.logic.GridType._


object GridControllerFactory {

  def apply(gridType: GridType)(cellSideSize: Int): IGridController = {

    gridType match {
      case HEXAGON => new HexagonalGridController(cellSideSize)
      case SQUARE => new SquareGridController(cellSideSize)
      case TRIANGLE => new TriangularGridController(cellSideSize)
      case _ => throw UnknownGridTypeException(s"Unknown grid type: $gridType")
    }

  }
}


case class UnknownGridTypeException(message: String) extends Exception(message)