package data_layer.settings

import breeze.linalg.DenseMatrix
import data_layer.geometry.{SimpleRange, AngleRange, Point2D, Point3D}
import data_layer.graphics.Color
import pureconfig.ConfigReader
import pureconfig.error.CannotConvert

import scala.util.{Failure, Success, Try}

object ConfigurationReader {

  implicit val rangeReader: ConfigReader[SimpleRange] = ConfigReader[(Double, Double)].emap {
    case (start, end) => Right(SimpleRange(start, end))
    case seq => Left(CannotConvert(seq.toString, "SimpleRange", "Cannot construct range"))
  }

  implicit val angleRangeReader: ConfigReader[AngleRange] = ConfigReader[(Double, Double)].emap {
    case seq @ (start, end) => Try(AngleRange(start, end)) match {
      case Success(range) => Right(range)
      case Failure(throwable) => Left(
        CannotConvert(seq.toString, "AngleRange", throwable.getMessage)
      )
    }
    case seq => Left(CannotConvert(seq.toString, "AngleRange", "Cannot construct range"))
  }


  implicit val colorReader: ConfigReader[Color] = ConfigReader[Seq[Int]].emap { seq =>

    def tryCreateColor(a: Int, r: Int, g: Int, b: Int): Either[CannotConvert, Color] =
      Try(Color(a,r,g,b)) match {
        case Success(color) => Right(color)
        case Failure(throwable) => Left(CannotConvert((a,r,g,b).toString, "Color", throwable.getMessage))
      }
    seq match {
      case Seq(a, r, g, b) => tryCreateColor(a,r,g,b)
      case Seq(r,g,b) => tryCreateColor(255, r,g,b)
      case _ => Left(CannotConvert(seq.toString, "Color", "Color is constructed of three or four integers"))
    }
  }

  implicit val point2DReader: ConfigReader[Point2D] = ConfigReader[(Double, Double)].emap {
    case (x, y) => Right(Point2D(x, y))
  }

  implicit val point3DReader: ConfigReader[Point3D] = ConfigReader[(Double, Double, Double)].emap {
    case (x, y, z) => Right(Point3D(x, y, z))
  }

  implicit val matrixReader: ConfigReader[DenseMatrix[Double]] = ConfigReader[Seq[Seq[Double]]].emap { seq =>
    val expectedSize = seq.length
    if (seq.isEmpty || seq.exists(s => s.length != expectedSize)) {
      Left(CannotConvert(seq.toString, "Matrix", "Rotation matrix must be a non-empty square matrix"))
    } else {
      Right(DenseMatrix(seq: _*))
    }
  }

}
