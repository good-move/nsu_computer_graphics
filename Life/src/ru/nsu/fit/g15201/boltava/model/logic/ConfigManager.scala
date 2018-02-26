package ru.nsu.fit.g15201.boltava.model.logic

import java.io.{BufferedReader, BufferedWriter, File, FileWriter}
import java.nio.file.{Files, Paths}

import ru.nsu.fit.g15201.boltava.model.canvas.geometry.Point
import ru.nsu.fit.g15201.boltava.model.logic.utils._

import scala.io.Source

/**
  * Reads configuration file and retrieves grid parameters
  */
object ConfigManager {

  val MODEL_FILE_EXTENSION = "life"
  val MODEL_FILE_DESCRIPTION = "LIFE model file"

  def openGameModel(configPath: String): GameSettings = {
    if (!configPath.endsWith(".life")) {
      throw new RuntimeException(s"Configuration file extension must be .$MODEL_FILE_EXTENSION.")
    }

    if (!Files.exists(Paths.get(configPath))) {
      throw new RuntimeException(s"File $configPath doesn't exist")
    }

    val bufferedSource = Source.fromFile(configPath)
    val reader = bufferedSource.bufferedReader()

    try {
      val gridParameters = new GameSettings()
      val (width, height) = readDimensions(reader)
      gridParameters.height = height
      gridParameters.width = width
      gridParameters.borderWidth = reader.readLine().filterComments.toInt
      gridParameters.borderSize = reader.readLine().filterComments.toInt
      gridParameters.aliveCells = readAliveCells(reader)

      gridParameters
    } catch {
      case e: Exception => throw new RuntimeException("Failed to read grid configuration file. Check out config file structure description.", e)
    } finally {
      bufferedSource.close
    }
  }

  private def readDimensions(reader: BufferedReader): (Int, Int) = {
    val line = reader.readLine()
    val dimensions = line.filterComments.split(" ").map(s => s.toInt)
    val (width, height) = (dimensions(0), dimensions(1))
    (width, height)
  }

  private def readAliveCells(reader: BufferedReader): Array[(Int, Int)] = {
    val aliveCellsCount = reader.readLine().filterComments.toInt
    if (aliveCellsCount < 0) throw new RuntimeException("Number of alive cells must be a positive integer.")
    val aliveCells = new Array[(Int, Int)](aliveCellsCount)
    for (i <- aliveCells.indices) {
      val coords = reader.readLine().filterComments.split(" ").map(s => s.toInt)
      aliveCells(i) = (coords(0), coords(1))
    }

    aliveCells
  }

  def saveGameModel(configFile: File, gridParameters: GameSettings, aliveCells: Array[Point]): Unit = {
    var file = configFile
    if(!file.getName.endsWith(s".$MODEL_FILE_EXTENSION")) {
      file = new File(s"${file.getAbsolutePath}.$MODEL_FILE_EXTENSION")
    }
    val writer = new BufferedWriter(new FileWriter(file))

    // write grid dimensions
    writer.write(s"${gridParameters.width} ${gridParameters.height}")
    writer.write(" // Grid dimensions\n")

    // write border width
    writer.write(s"${gridParameters.borderWidth}")
    writer.write(" // Border width in pixels\n")

    // write cell side size
    writer.write(s"${gridParameters.borderSize}")
    writer.write(" // Cell side size in pixels\n")

    // write alive cells coordinates
    writer.write(s"${aliveCells.length}")
    writer.write(" // Number of alive cells\n")

    for (point <- aliveCells) {
      writer.write(s"${point.x} ${point.y}\n")
    }

    writer.flush()
  }

}

object utils {

  val COMMENT_SIGN = "//"

  implicit class StringExtension(val s: String) {
    def filterComments: String = s.split(COMMENT_SIGN)(0).trim
  }

}
