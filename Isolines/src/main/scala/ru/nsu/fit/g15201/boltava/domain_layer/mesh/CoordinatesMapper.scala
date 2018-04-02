package ru.nsu.fit.g15201.boltava.domain_layer.mesh

import ru.nsu.fit.g15201.boltava.domain_layer.logic.function.FiniteDomain2D
import ru.nsu.fit.g15201.boltava.domain_layer.primitives.{Dimensions, Point2D}

object CoordinatesMapper {

  private var functionDx: Double = 0
  private var functionDy: Double = 0
  private var domainXOffset: Double = 0
  private var domainYOffset: Double = 0

  private var fieldDx: Double = 0
  private var fieldDy: Double = 0

  def setMapping(fieldDimensions: Dimensions, functionDomain: FiniteDomain2D): Unit = {
    domainXOffset = functionDomain.xRange.lower.value
    domainYOffset = functionDomain.yRange.lower.value

    functionDx = functionDomain.xRange.size.value / fieldDimensions.width
    functionDy = functionDomain.yRange.size.value / fieldDimensions.height

    fieldDx = fieldDimensions.width / functionDomain.xRange.size.value
    fieldDy = fieldDimensions.height / functionDomain.yRange.size.value
  }

  def toDomain(fieldPoint: Point2D): Point2D = {
    toDomain(fieldPoint.x, fieldPoint.y)
  }

  def toDomain(x: Double, y: Double): Point2D = {
    val domainX = x * functionDx + domainXOffset
    val domainY = y * functionDy + domainYOffset
    Point2D(domainX, domainY)
  }

  def toField(domainPoint: Point2D): Point2D = {
    toField(domainPoint.x, domainPoint.y)
  }

  def toField(x: Double, y: Double): Point2D = {
    val fieldX = fieldDx * (x - domainXOffset)
    val fieldY = fieldDy * (y - domainYOffset)
    Point2D(fieldX, fieldY)
  }

}
