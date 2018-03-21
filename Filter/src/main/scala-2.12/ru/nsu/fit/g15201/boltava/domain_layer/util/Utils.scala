package ru.nsu.fit.g15201.boltava.domain_layer.util

object Utils {

  def withTime[R](body: => R): R = {
    val startTime = System.nanoTime()
    val result = body
    val endTime = System.nanoTime()
    println(s"Execution time: ${endTime - startTime}ns")
    result
  }

}
