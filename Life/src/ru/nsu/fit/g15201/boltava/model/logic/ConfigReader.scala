package ru.nsu.fit.g15201.boltava.model.logic

import java.io.BufferedReader
import java.nio.file.{Files, Paths}

import scala.io.Source


/**
  * Reads configuration file and retrieves grid parameters
  */
object ConfigReader {

  def parseConfig(configPath: String): GridParameters = {

    if (!Files.exists(Paths.get(configPath))) {
      throw new RuntimeException(s"File $configPath doesn't exist")
    }

    val bufferedSource = Source.fromFile(configPath)
    val reader = bufferedSource.bufferedReader()

    try {
      val gridParameters = new GridParameters()
      val (width, height) = readDimensions(reader)
      gridParameters.height = height
      gridParameters.width = width
      gridParameters.borderWidth = reader.readLine().toInt
      gridParameters.cellSideSize = reader.readLine().toInt
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
    val dimensions = line.split(" ").map(s => s.toInt)
    val (width, height) = (dimensions(0), dimensions(1))
    (width, height)
  }

  private def readAliveCells(reader: BufferedReader): Array[(Int, Int)] = {
    val aliveCellsCount = reader.readLine().toInt
    if (aliveCellsCount <= 0) throw new RuntimeException("Number of alive cells must be a positive integer.")
    val aliveCells = new Array[(Int, Int)](aliveCellsCount)
    for (i <- aliveCells.indices) {
      val coords = reader.readLine().split(" ").map(s => s.toInt)
      aliveCells(i) = (coords(0), coords(1))
    }

    aliveCells
  }

}
