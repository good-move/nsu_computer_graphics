package ru.nsu.fit.g15201.boltava.domain_layer.mesh

import ru.nsu.fit.g15201.boltava.domain_layer.logic.function.FiniteDomain2D
import ru.nsu.fit.g15201.boltava.domain_layer.primitives.{Dimensions, Point2D}

trait ICoordinatesMapper {

  def setMapping(fieldDimensions: Dimensions, functionDomain: FiniteDomain2D): Unit

  def toDomain(fieldPoint: Point2D): Point2D
  def toDomain(x: Double, y: Double): Point2D

  def toField(domainPoint: Point2D): Point2D
  def toField(x: Double, y: Double): Point2D

}

object CoordinatesMapper extends ICoordinatesMapper {

  private var functionDx: Double = 0
  private var functionDy: Double = 0
  private var domainXOffset: Double = 0
  private var domainYOffset: Double = 0

  private var fieldDx: Double = 0
  private var fieldDy: Double = 0

  override def setMapping(fieldDimensions: Dimensions, functionDomain: FiniteDomain2D): Unit = {
    domainXOffset = functionDomain.xRange.start.value
    domainYOffset = functionDomain.yRange.start.value

    functionDx = functionDomain.xRange.size.value / fieldDimensions.width
    functionDy = functionDomain.yRange.size.value / fieldDimensions.height

    fieldDx = fieldDimensions.width / functionDomain.xRange.size.value
    fieldDy = fieldDimensions.height / functionDomain.yRange.size.value
  }

  override def toDomain(fieldPoint: Point2D): Point2D = {
    toDomain(fieldPoint.x, fieldPoint.y)
  }

  override def toDomain(x: Double, y: Double): Point2D = {
    val domainX = x * functionDx + domainXOffset
    val domainY = y * functionDy + domainYOffset
    Point2D(domainX, domainY)
  }

  override def toField(domainPoint: Point2D): Point2D = {
    toField(domainPoint.x, domainPoint.y)
  }

  override def toField(x: Double, y: Double): Point2D = {
    val fieldX = fieldDx * (x - domainXOffset)
    val fieldY = fieldDy * (y - domainYOffset)
    Point2D(fieldX, fieldY)
  }

}
