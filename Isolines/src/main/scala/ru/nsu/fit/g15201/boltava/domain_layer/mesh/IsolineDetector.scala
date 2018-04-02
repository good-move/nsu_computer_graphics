package ru.nsu.fit.g15201.boltava.domain_layer.mesh

import ru.nsu.fit.g15201.boltava.domain_layer.primitives.{Point2D, Segment}

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

object IsolineDetector {

  def clearIsolines(): Unit = isolinesStorage.clear()

  private val isolinesStorage = new IsolinesStorage()

  def isolines: Seq[Segment] = isolinesStorage.list

  def calculateIsoLevels(min: Double, max: Double, levelCount: Int): Seq[Double] = {
    val step = (max-min).abs / (levelCount+1)
    for {
      level <- min+step until max by step
    } yield level
  }

  def buildIsolines(cell: Cell, isoLevels: Seq[Double]): Unit = {
    isoLevels.foreach(level => buildIsolinesForLevel(cell, level))
  }

  def buildIsolinesForLevel(cell: Cell, isoLevel: Double): Unit = {

    def triggerActive(controlNode: ControlNode): Unit = {
      controlNode.aboveIsoLevel = controlNode.position.z >= isoLevel
    }

    triggerActive(cell.topLeft)
    triggerActive(cell.topRight)
    triggerActive(cell.bottomRight)
    triggerActive(cell.bottomLeft)

    cell.configuration = getCellConfiguration(cell).value

    _buildIsolines(cell)(isoLevel)
  }

  private def getCellConfiguration(cell: Cell): CellConfiguration = {
    var configuration = 0

    if (cell.topLeft.aboveIsoLevel)
      configuration += 8
    if (cell.topRight.aboveIsoLevel)
      configuration += 4
    if (cell.bottomRight.aboveIsoLevel)
      configuration += 2
    if (cell.bottomLeft.aboveIsoLevel)
      configuration += 1

    CellConfiguration(configuration)
  }

  private def _buildIsolines(cell: Cell)(implicit isoLevel: Double): Unit = {
    val top = findIntersection(cell.topLeft, cell.topRight, IntersectionAxis.Ox)
    val right = findIntersection(cell.topRight, cell.bottomRight, IntersectionAxis.Oy)
    val bottom = findIntersection(cell.bottomRight, cell.bottomLeft, IntersectionAxis.Ox)
    val left = findIntersection(cell.topLeft, cell.bottomLeft, IntersectionAxis.Oy)
    val avg = (cell.topLeft.position.z + cell.topRight.position.z + cell.bottomRight.position.z + cell.bottomLeft.position.z)/4

    cell.configuration match {
        // all nodes inactive => all corner values are below isoLevel => no intersections
        // or
        // all nodes active => all corner values are above isoLevel => no intersections
      case 0 | 15 =>

        // cases with one or three active node(s)
      case 1 =>
        addSegment(cell, isoLevel, left, bottom)
      case 2 =>
        addSegment(cell, isoLevel, bottom, right)
      case 4 =>
        addSegment(cell, isoLevel, right, top)
      case 8 =>
        addSegment(cell, isoLevel, top, left)

        // cases with two active nodes
      case 6 | 9 =>
        addSegment(cell, isoLevel, top, bottom)
      case 3 | 12 =>
        addSegment(cell, isoLevel, left, right)
      case 5 | 10 =>
        if (avg < isoLevel) cell.configuration = ~cell.configuration & 0x0000000f
        cell.configuration match {
          case 5 =>
            addSegment(cell, isoLevel, bottom, right)
            addSegment(cell, isoLevel, top, left)
          case 10 =>
            addSegment(cell, isoLevel, bottom, left)
            addSegment(cell, isoLevel, top, right)
        }
//      // cases with three active nodes
      case 7 =>
        addSegment(cell, isoLevel, top, left)
      case 11 =>
        addSegment(cell, isoLevel, top, right)
      case 13 =>
        addSegment(cell, isoLevel, bottom, right)
      case 14 =>
        addSegment(cell, isoLevel, bottom, left)
    }
  }

  private object IntersectionAxis extends Enumeration {
    type InterpolationAxis = Value
    val Ox, Oy = Value
  }

  private def findIntersection(node1: ControlNode, node2: ControlNode, intersectionAxis: IntersectionAxis.Value)(implicit isoLevel: Double): Point2D = {
    val projectedDifference = intersectionAxis match {
      case IntersectionAxis.Ox => (node1.position.x - node2.position.x).abs
      case IntersectionAxis.Oy => (node1.position.y - node2.position.y).abs
    }

    val (above, below) =
      if (node1.position.z > isoLevel) (node1, node2)
      else (node2, node1)

    val valueDifference = above.position.z - below.position.z
    val isoDifference = isoLevel - below.position.z

    val interpolatedValue = projectedDifference * isoDifference / valueDifference
    val minX = node1.position.x.min(node2.position.x)
    val maxX = node1.position.x.max(node2.position.x)
    val minY = node1.position.y.min(node2.position.y)
    val maxY = node1.position.y.max(node2.position.y)

    intersectionAxis match {
      case IntersectionAxis.Ox if minX == above.position.x =>
        Point2D(maxX - interpolatedValue, node1.position.y)
      case IntersectionAxis.Ox if minX == below.position.x =>
        Point2D(interpolatedValue + minX, node1.position.y)
      case IntersectionAxis.Oy if minY == above.position.y =>
        Point2D(node1.position.x, maxY - interpolatedValue)
      case IntersectionAxis.Oy if minY == below.position.y =>
        Point2D(node1.position.x, interpolatedValue + minY)
    }
  }

  private def addSegment(cell: Cell, isoLevel: Double, point1: Point2D, point2: Point2D): Unit = {
    val segment = Segment(point1, point2)
    isolinesStorage.put(IsoLevel(isoLevel), segment)
  }

}


case class IsoLevel(value: Double) extends AnyVal


private class IsolinesStorage {

  val map = mutable.HashMap.empty[Double, ListBuffer[Segment]]

  def get(level: IsoLevel): Option[Seq[Segment]] = map.get(level.value)

  def put(level: IsoLevel, segment: Segment): Unit = {
    map
      .getOrElseUpdate(level.value, ListBuffer.empty[Segment])
      .append(segment)
  }

  def list: Seq[Segment] = map.values.flatten.toSeq

  def clear(): Unit = map.clear()

}