package ru.nsu.fit.g15201.boltava.model.logic

import ru.nsu.fit.g15201.boltava.model.canvas.IGridController
import ru.nsu.fit.g15201.boltava.model.canvas.geometry.Point
import ru.nsu.fit.g15201.boltava.model.logic.State.State


/**
  * Implements Conway Game Of Life state switching, given array
  * of cells and grid controller to fetch information about cells' neighbors
  *
  */
class ConwayFieldUpdater extends Runnable {

  private var gridController: IGridController = _
  private var mainField: Array[Array[Cell]] = _
  private var helperField: Array[Array[State]] = _
  private var fieldStateObserver: IFieldStateObserver = _

  private var _closeNeighborsImpactScore: Double = 1.0
  private var _distantNeighborsImpactScore: Double = .3
  private var _lifeLowerBorderScore: Double = 2.0
  private var _lifeUpperBorderScore: Double = 3.3
  private var _birthLowerBorderScore: Double = 2.3
  private var _birthUpperBorderScore: Double = 2.9


  def run(): Unit = {
    makeStep()
  }

  def makeStep(): Unit = {
    _nextStep()
    fieldStateObserver.onFieldUpdated(mainField)
  }

  private def _nextStep(): Unit = {
    if (mainField == null) {
      throw new RuntimeException("Field is not initialized for field updater")
    }

    println("Next step")

    for (i <- mainField.indices; j <- mainField(i).indices) {
      // TODO: optimize neighbors counting performance?

      val closeNeighborsCount = gridController.getCellNeighbors((i, j))
        .filter(neighborsFilter).count(p => countAliveFilter(p, mainField))
      val distantNeighborsCount = gridController.getCellDistantNeighbors((i, j))
        .filter(neighborsFilter).count(p => countAliveFilter(p, mainField))

      val impact = closeNeighborsCount * closeNeighborsImpactScore +
        distantNeighborsCount * distantNeighborsImpactScore

      val cell = mainField(i)(j)
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

    for (i <- helperField.indices; j <- helperField(i).indices) {
      mainField(i)(j).setState(helperField(i)(j))
    }

    println("step done")
  }

  def setMainField(field: Array[Array[Cell]]): Unit = {
    // TODO: make synchronized
    mainField = field
    helperField = createHelperArray()
  }

  private def neighborsFilter(p: Point): Boolean = {
    p.x >= 0 && p.y >= 0 && p.x < mainField.length && p.y < mainField(0).length
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

  def setGridController(gridController: IGridController): Unit = {
    this.gridController = gridController
  }

  private def createHelperArray(): Array[Array[State]] = {
    val result = new Array[Array[State]](mainField.length)
    for (i <- result.indices) {
      result(i) = new Array[State](mainField(i).length)
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
