package ru.nsu.fit.g15201.boltava.model.logic

import ru.nsu.fit.g15201.boltava.model.canvas.IGridController
import ru.nsu.fit.g15201.boltava.model.canvas.geometry.Point
import ru.nsu.fit.g15201.boltava.model.logic.State.State


class ConwayFieldUpdater(private var initialField: Array[Array[Cell]],
                         private val gridController: IGridController) extends Runnable {

  private val helperField = createHelperArray()
  private var fieldStateObserver: IFieldStateObserver = _

  private var sourceHelper = false

  private var _closeNeighborsImpactScore: Double = 1.0
  private var _distantNeighborsImpactScore: Double = .3
  private var _lifeLowerBorderScore: Double = 2.0
  private var _lifeUpperBorderScore: Double = 3.3
  private var _birthLowerBorderScore: Double = 2.3
  private var _birthUpperBorderScore: Double = 2.9

  override def run(): Unit = {
    nextStep()
    fieldStateObserver.onFieldUpdated(initialField)
  }

  def nextStep(): Unit = {
    println("Next step")

    for (i <- initialField.indices) {
      for (j <- initialField(i).indices) {
        // TODO: optimize neighbors counting performance?


        val closeNeighborsCount = gridController.getCellNeighbors((i, j))
          .filter(neighborsFilter).count(p => countAliveFilter(p, initialField))
        val distantNeighborsCount = gridController.getCellDistantNeighbors((i, j))
          .filter(neighborsFilter).count(p => countAliveFilter(p, initialField))

        val impact = closeNeighborsCount * closeNeighborsImpactScore +
          distantNeighborsCount * distantNeighborsImpactScore

        val cell = initialField(i)(j)
        if (cell.getState == State.DEAD) {
          val canBeBorn = birthLowerBorderScore <= impact && impact <= birthUpperBorderScore
          if (canBeBorn) {
            helperField(i)(j) = State.ALIVE
          } else {
            helperField(i)(j) = State.DEAD
          }
        } else if (cell.getState == State.ALIVE) {
          val isStillAlive = lifeLowerBorderScore <= impact && impact <= lifeUpperBorderScore
          if (!isStillAlive) {
            helperField(i)(j) = State.DEAD
          } else {
            helperField(i)(j) = State.ALIVE
          }
        }
      }
    }

    for (i <- helperField.indices) {
      for (j <- helperField(i).indices) {
        initialField(i)(j).setState(helperField(i)(j))
      }
    }

  }

  def setInitialField(field: Array[Array[Cell]]): Unit = {
    // TODO: make synchronized
    initialField = field
    sourceHelper = false
  }

  private def neighborsFilter(p: Point): Boolean = {
    p.x >= 0 && p.y >= 0 && p.x < initialField.length && p.y < initialField(0).length
  }

  private def countAliveFilter(p: Point, field: Array[Array[Cell]]): Boolean = {
    field(p.x)(p.y).getState == State.ALIVE
  }

  def setStateObserver(fieldStateObserver: IFieldStateObserver): Unit = {
    // TODO: make synchronized
    this.fieldStateObserver = fieldStateObserver
  }

  def removeStateObserver(): Unit = {
    // TODO: make synchronized
    this.fieldStateObserver = null
  }

  private def createHelperArray(): Array[Array[State]] = {
    val result = new Array[Array[State]](initialField.length)
    for (i <- result.indices) {
      result(i) = new Array[State](initialField(i).length)
    }
    result
  }


  // ******************************** Getters and Setters ********************************
  // TODO: make all getters/setters sync?
  def closeNeighborsImpactScore: Double = _closeNeighborsImpactScore

  def closeNeighborsImpactScore_= (value: Double): Unit = {
    _closeNeighborsImpactScore = value
  }


  def distantNeighborsImpactScore: Double = _distantNeighborsImpactScore

  def distantNeighborsImpactScore_=(value: Double): Unit = {
    _distantNeighborsImpactScore = value
  }


  def lifeLowerBorderScore: Double = _lifeLowerBorderScore

  def lifeLowerBorderScore_=(value: Double): Unit = {
    _lifeLowerBorderScore = value
  }


  def lifeUpperBorderScore: Double = _lifeUpperBorderScore

  def lifeUpperBorderScore_=(value: Double): Unit = {
    _lifeUpperBorderScore = value
  }


  def birthLowerBorderScore: Double = _birthLowerBorderScore

  def birthLowerBorderScore_=(value: Double): Unit = {
    _birthLowerBorderScore = value
  }


  def birthUpperBorderScore: Double = _birthUpperBorderScore

  def birthUpperBorderScore_=(value: Double): Unit = {
    _birthUpperBorderScore = value
  }

}
