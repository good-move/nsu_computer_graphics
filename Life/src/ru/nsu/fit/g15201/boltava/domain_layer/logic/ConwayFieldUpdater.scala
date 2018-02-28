package ru.nsu.fit.g15201.boltava.domain_layer.logic

import ru.nsu.fit.g15201.boltava.domain_layer.canvas.IGridController
import ru.nsu.fit.g15201.boltava.domain_layer.canvas.geometry.Point
import ru.nsu.fit.g15201.boltava.domain_layer.logic.State.State
import ru.nsu.fit.g15201.boltava.domain_layer.logic.settings.{ImpactScores, LifeScores}
import ru.nsu.fit.g15201.boltava.domain_layer.utils.Extensions._


/**
  * Implements Conway Game Of Life state switching, given array
  * of cells and grid controller to fetch information about cells' neighbors
  *
  */
class ConwayFieldUpdater extends Runnable {

  private var gridController: IGridController = _
  private var mainField: Array[Array[Cell]] = _
  private var helperField: Array[Array[State]] = _
  private var fieldStateObserver: Option[IFieldStateObserver] = None

  private var closeNeighborsImpactScore: Double = 1.0
  private var distantNeighborsImpactScore: Double = .3
  private var minAliveScore: Double = 2.0
  private var maxAliveScore: Double = 3.3
  private var minBirthScore: Double = 2.3
  private var maxBirthScore: Double = 2.9


  def run(): Unit = {
    makeStep()
  }

  def makeStep(): Unit = {
    _nextStep()
    if (fieldStateObserver.isDefined) {
      fieldStateObserver.get.onFieldUpdated(mainField)
    }
  }

  private def _nextStep(): Unit = {
    if (mainField == null) {
      throw new RuntimeException("Field is not initialized for field updater")
    }

    for (i <- mainField.indices; j <- mainField(i).indices) {
      // TODO: optimize neighbors counting performance?

      val closeNeighborsCount = gridController.getCellNeighbors((i, j))
        .filter(neighborsFilter).count(p => countAliveFilter(p, mainField))
      val distantNeighborsCount = gridController.getCellDistantNeighbors((i, j))
        .filter(neighborsFilter).count(p => countAliveFilter(p, mainField))

      val impactScore = closeNeighborsCount * closeNeighborsImpactScore +
        distantNeighborsCount * distantNeighborsImpactScore

      val cell = mainField(i)(j)
      if (cell.getState == State.DEAD) {
        val canBeBorn = impactScore.within(minBirthScore, maxBirthScore)
        if (canBeBorn) {
          helperField(i)(j) = State.ALIVE
        } else {
          helperField(i)(j) = State.DEAD
        }
      } else if (cell.getState == State.ALIVE) {
        val isStillAlive = impactScore.within(minAliveScore, maxAliveScore)
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
  }

  def setMainField(field: Array[Array[Cell]]): Unit = {
    synchronized {
      mainField = field
      helperField = createHelperArray()
    }
  }

  private def neighborsFilter(point: Point): Boolean = {
    point.x >= 0 && point.y >= 0 && point.x < mainField.length && point.y < mainField(0).length
  }

  private def countAliveFilter(p: Point, field: Array[Array[Cell]]): Boolean = {
    field(p.x)(p.y).getState == State.ALIVE
  }

  def setStateObserver(fieldStateObserver: IFieldStateObserver): Unit = {
    synchronized {
      this.fieldStateObserver = Some(fieldStateObserver)
    }
  }

  def removeStateObserver(): Unit = {
    synchronized {
      this.fieldStateObserver = None
    }
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

  def updateImpactScore(impactScores: ImpactScores): Unit = {
    synchronized {
      closeNeighborsImpactScore = impactScores.firstOrderImpact
      distantNeighborsImpactScore = impactScores.secondOrderImpact
    }
  }

  def updateLifeScores(lifeScores: LifeScores): Unit = {
    synchronized {
      minAliveScore = lifeScores.minAliveScore
      maxAliveScore = lifeScores.maxAliveScore

      minBirthScore = lifeScores.minBirthScore
      maxBirthScore = lifeScores.maxBirthScore
    }
  }


}
