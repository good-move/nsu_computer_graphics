package ru.nsu.fit.g15201.boltava.domain_layer.logic.settings

import java.io.BufferedReader
import java.nio.file.{Files, Paths}

import ru.nsu.fit.g15201.boltava.domain_layer.primitives.Color

import scala.collection.mutable.ListBuffer
import scala.io.Source
import scala.util.{Failure, Success, Try}


// ************************** Reader **************************

object ConfigReader {
  import utils._

  def read(path: String): Settings = {
    if (!Files.exists(Paths.get(path))) {
      throw new RuntimeException(s"File $path doesn't exist")
    }

    val reader = Source.fromFile(path).bufferedReader

    try {
      val (xGridSize, yGridSize) = readGridSize(reader)
      val legendColors = readLegendColors(reader)
      val colorLevels = legendColors.length
      val isolineColor = readIsolineColor(reader)
      Settings(
        xGridSize, yGridSize,
        colorLevels,
        isolineColor,
        legendColors
      )
    } catch {
      case e: ConfigurationReaderError => throw e
      case e: Exception => throw ConfigurationReaderError(s"Failed to read configuration file: ${e.getMessage}")
    } finally {
      reader.close()
    }
  }

  private def readGridSize(reader: BufferedReader): (Int, Int) = {
    val dimensions = reader.readLine.filterComments.split(" ").toList.toIntList.filterRight

    dimensions match {
      case xSize :: ySize :: Nil => (xSize, ySize)
      case _ => throw ConfigurationReaderError("""Invalid grid dimensions. Expected "xSize ySize" of type integer""")
    }
  }

  private def readLegendColors(reader: BufferedReader): Seq[Color] = {
    val colorLevels = reader.readLine.filterComments.toIntOption

    if (colorLevels.isEmpty) {
      throw ConfigurationReaderError(s"Failed to read color levels value. Expecting an integer")
    }

    val list = ListBuffer[Color]()
    for (_ <- 0 until colorLevels.get) {
      val legendColor = reader.readLine.filterComments.split(" ").toList.toIntList.filterRight
      legendColor match {
        case red :: green :: blue :: Nil => list += new Color(red, green, blue)
        case _ => throw ConfigurationReaderError(
          s"""Failed to read legend color. Expected "r g b" values of type integer, got $legendColor"""
        )
      }
    }

    list
  }

  private def readIsolineColor(reader: BufferedReader): Color = {
    val colors = reader.readLine.filterComments.split(" ").toList.toIntList.filterRight
    colors match {
      case red :: green :: blue :: Nil => new Color(red, green, blue)
      case _ => throw ConfigurationReaderError(
        s"""Invalid isoline color value. Expected "r g b" values of type integer, got $colors"""
      )
    }
  }

}

// ************************** Exception **************************
case class ConfigurationReaderError(message: String) extends Exception


// ************************** Reader helpers **************************

object utils {

  private val COMMENT_SIGN = "//"

  implicit class StringExtension(val s: String) {

    def filterComments: String = s.split(COMMENT_SIGN)(0).trim

    def toIntOption: Option[Int] = {
      Try(s.toInt) match {
        case Success(value) => Some(value)
        case _ => None
      }
    }

  }

  implicit class ListExtension(val list: List[String]) {

    def toIntList: List[Either[String, Int]] = list match {
      case head :: tail => tryConvertToInt(head) :: tail.toIntList
      case _ => Nil
    }

    private def tryConvertToInt(string: String): Either[String, Int] = {
      Try(string.toInt) match {
        case Success(value) => Right(value)
        case Failure(_) => Left(string)
      }
    }

  }

  implicit class ListOfEither[L, R](val list: List[Either[L, R]]) {

    def filterRight: List[R] = list match {
      case head :: tail => head match {
        case Right(v) => v :: tail.filterRight
        case _ => tail.filterRight
      }
      case _ => Nil
    }
  }

}
