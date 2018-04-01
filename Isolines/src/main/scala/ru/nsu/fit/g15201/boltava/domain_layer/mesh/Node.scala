package ru.nsu.fit.g15201.boltava.domain_layer.mesh

import ru.nsu.fit.g15201.boltava.domain_layer.primitives.{Point2D, Point3D}

trait INode {
  val position: Point3D
}

case class Node(position: Point3D) extends INode {
  var vertexIndex: Int = -1
}

case class ControlNode(position: Point3D) extends INode {
  var _active: Boolean = false

  def aboveIsoLevel: Boolean = _active
  def aboveIsoLevel_=(isActive: Boolean) : Unit = _active = isActive
}

case class Cell(topLeft : ControlNode,
                topRight: ControlNode,
                bottomRight: ControlNode,
                bottomLeft: ControlNode) {
  var configuration: Int = 0
}

case class CellConfiguration(value: Int) extends AnyVal