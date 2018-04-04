package ru.nsu.fit.g15201.boltava.domain_layer.logic.function

abstract class Function2D(protected val _mathDomain: Domain2D) extends IFunction2D {

  private var _domain: Option[FiniteDomain2D] = None

  override def mathDomain: Domain2D = _mathDomain

  override def domain: Option[FiniteDomain2D] = _domain

  override def domain_=(domain: FiniteDomain2D): Unit = {
    if (domain.xRange.start >= _mathDomain.xRange.lower &&
      domain.xRange.end <= _mathDomain.xRange.upper &&
      domain.yRange.start >= _mathDomain.yRange.lower &&
      domain.yRange.end <= _mathDomain.yRange.upper
    ) _domain = Some(domain)
    else throw DomainException("Local domain doesn't fit in function's global domain")
  }

  override def apply(x: Double, y: Double): Double = {
    _domain match {
      case Some(domain) => calculate(x, y)
//      case Some(domain) if domain.contains(x, y) => calculate(x, y)
//      case Some(_) => throw DomainException(s"Point ($x,$y) doesn't belong to function domain")
      case None => throw new IllegalStateException("Domain is not set")
    }
  }

  def calculate(x: Double, y: Double): Double

}
