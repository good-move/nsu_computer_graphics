package ru.nsu.fit.g15201.boltava.domain_layer.controllers

import ru.nsu.fit.g15201.boltava.domain_layer.data.FileExtension
import ru.nsu.fit.g15201.boltava.domain_layer.logic.function.{EllipticHyperboloid, FiniteDomain2D, Function2D, SinCosProduct}
import ru.nsu.fit.g15201.boltava.domain_layer.logic.settings.{ConfigReader, Settings}
import ru.nsu.fit.g15201.boltava.domain_layer.mesh.MeshGenerator.CellGrid
import ru.nsu.fit.g15201.boltava.domain_layer.mesh.{CoordinatesMapper, IsoLevel, IsolinesController, MeshGenerator}
import ru.nsu.fit.g15201.boltava.domain_layer.primitives.{Dimensions, Point2D}
import ru.nsu.fit.g15201.boltava.presentation_layer.menu.Contract.{IMenuInteractor, IMenuPresenter}
import ru.nsu.fit.g15201.boltava.presentation_layer.workbench.Contract
import ru.nsu.fit.g15201.boltava.presentation_layer.workbench.Contract.{IWorkbenchInteractor, IWorkbenchPresenter}

import scala.collection.mutable
import scala.util.{Failure, Success, Try}

class MainController {

  private val function: Function2D = new SinCosProduct
  private var cellGrid: Option[CellGrid] = None
  private var isoLevels: Option[Seq[Double]] = None
  private var currentFieldDimensions = Dimensions(500, 500)

  private var modelInitialized = false

  val workbenchInteractor = new WorkbenchInteractor
  val menuInteractor = new MenuInteractor

  {
    menuInteractor.subscribe(workbenchInteractor)
  }



  //  *********************** Workbench Interactor ***********************
  class WorkbenchInteractor extends IWorkbenchInteractor with ILayerVisibilityObserver {

    private var presenter: Option[IWorkbenchPresenter] = None

    override def functionValue(point: Point2D): Double = {
      val domainPoint = CoordinatesMapper.toDomain(point)
      function(domainPoint.x, domainPoint.y)
    }

    override def createIsoline(level: IsoLevel): Unit = {
      for (cell <- cellGrid.get.grid) {
        IsolinesController.buildRawIsolineForLevel(cell, level.value)
      }
      IsolinesController.mapToFieldIsolines(CoordinatesMapper)
      // TODO: optimize to redraw only the new isoline
      updateField()
    }

    override def handleWindowResize(fieldDimensions: Dimensions): Unit = {
      if (modelInitialized) {
        currentFieldDimensions = fieldDimensions
        cellGrid = Some(MeshGenerator.generate(fieldDimensions, menuInteractor.settings.get, function, CoordinatesMapper))
        CoordinatesMapper.setMapping(fieldDimensions, function.domain.get)
        IsolinesController.clearMapped()
        IsolinesController.mapToFieldIsolines(CoordinatesMapper)
        updateField()
      }
    }

    override def setPresenter(presenter: Contract.IWorkbenchPresenter): Unit = {
      this.presenter = Some(presenter)
    }

    override def onGridVisibilityChanged(visible: Boolean): Unit = {
      presenter.get.setShowGrid(visible)
    }

    override def onIntersectionPointsVisibilityChanged(visible: Boolean): Unit = {
      presenter.get.setShowIntersectionPoints(visible)
    }

    override def onIsolinesVisibilityChanged(visible: Boolean): Unit = {
      presenter.get.setShowIsolines(visible)
    }

    def updateField(): Unit = {
      presenter.get.redrawIsolines(IsolinesController.mappedIsolines)
      presenter.get.redrawGrid(cellGrid.get.cellWidth, cellGrid.get.cellHeight)
      presenter.get.redrawIntersectionPoints(IsolinesController.mappedIsolines)
    }

  }



//  *********************** Menu Interactor ***********************
  class MenuInteractor extends IMenuInteractor with ILayerVisibilityProvider {

    private var _settings: Option[Settings] = None
    private var presenter: Option[IMenuPresenter] = None

    private var gridVisible = false
    private var isolinesVisible = false
    private var intersectionsVisible = false

    private val subscribers = mutable.HashSet.empty[ILayerVisibilityObserver]

    private val CONFIG_FILE_EXTENSION = "isoline"

    def settings: Option[Settings] = _settings

    override def toggleIsolinesDisplay(): Unit = {
      isolinesVisible = !isolinesVisible
      subscribers.foreach(s => s.onIsolinesVisibilityChanged(isolinesVisible))
    }

    override def toggleGridDisplay(): Unit = {
      gridVisible = !gridVisible
      subscribers.foreach(s => s.onGridVisibilityChanged(gridVisible))
    }

    override def toggleIntersectionsDisplay(): Unit = {
      intersectionsVisible = !intersectionsVisible
      subscribers.foreach(s => s.onIntersectionPointsVisibilityChanged(intersectionsVisible))
    }

    override def toggleInterpolationDisplay(): Unit = {
      println("toggleInterpolationDisplay() invoked")
    }

    override def beforeExit(): Unit = {
      println("beforeExit() invoked")
    }

    override def openModel(filePath: String): Unit = {
      Try(ConfigReader.read(filePath)) match {
        case Success(newSettings) =>
          modelInitialized = true
          _settings = Some(newSettings)
          reloadApp()
        case Failure(throwable) =>
          if (presenter.isDefined) {
            presenter.get.showError("Failed to open model", throwable.getMessage)
          } else {
            throw new RuntimeException("Presenter is not set")
          }
      }
    }

    override def modelFileExtension: FileExtension = FileExtension(CONFIG_FILE_EXTENSION)

    override def setPresenter(presenter: IMenuPresenter): Unit = {
      this.presenter = Some(presenter)
    }

    private def reloadApp(): Unit = {
      function.domain = FiniteDomain2D(-10, 10, -10, 10)
      CoordinatesMapper.setMapping(currentFieldDimensions, function.domain.get)
      cellGrid = Some(MeshGenerator.generate(currentFieldDimensions, _settings.get, function, CoordinatesMapper))
      isoLevels = Some(IsolinesController.calculateIsoLevels(-1, 1, _settings.get.levels))
      println(s"IsoLevels: $isoLevels")
      IsolinesController.clearAll()
      cellGrid.get.grid.foreach { cell =>
        IsolinesController.buildRawIsolines(cell, isoLevels.get)
      }
      IsolinesController.mapToFieldIsolines(CoordinatesMapper)
      workbenchInteractor.updateField()
    }

    override def subscribe(visibilityObserver: ILayerVisibilityObserver): Unit = {
      subscribers.add(visibilityObserver)
    }

    override def unsubscribe(visibilityObserver: ILayerVisibilityObserver): Unit = {
      subscribers.remove(visibilityObserver)
    }

  }

}
