package ru.nsu.fit.g15201.boltava.domain_layer.controllers

import ru.nsu.fit.g15201.boltava.domain_layer.data.FileExtension
import ru.nsu.fit.g15201.boltava.domain_layer.logic.function.{EllipticHyperboloid, FiniteDomain2D, Function2D, SinCosProduct}
import ru.nsu.fit.g15201.boltava.domain_layer.logic.settings.{ConfigReader, FieldParameters, Settings}
import ru.nsu.fit.g15201.boltava.domain_layer.mesh.MeshGenerator.CellGrid
import ru.nsu.fit.g15201.boltava.domain_layer.mesh.{CoordinatesMapper, IsoLevel, IsolinesController, MeshGenerator}
import ru.nsu.fit.g15201.boltava.domain_layer.primitives.{Color, ColorHelpers, Dimensions, Point2D}
import ru.nsu.fit.g15201.boltava.presentation_layer.menu.Contract.{IMenuInteractor, IMenuPresenter}
import ru.nsu.fit.g15201.boltava.presentation_layer.settings.Contract.{ISettingsInteractor, ISettingsPresenter}
import ru.nsu.fit.g15201.boltava.presentation_layer.workbench.Contract.{ColorMapMode, IWorkbenchInteractor, IWorkbenchPresenter}

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.util.{Failure, Success, Try}

class MainController {

  private var _settings: Option[Settings] = None
  private val function: Function2D = new EllipticHyperboloid
  private var cellGrid: Option[CellGrid] = None
  private var isoLevels: Option[Seq[Double]] = None
  private val customIsoLevels: ListBuffer[Double] = ListBuffer.empty
  private var currentFieldDimensions = Dimensions(735, 400)

  private var gridVisible = false
  private var isolinesVisible = false
  private var intersectionsVisible = false
  private var colorMapVisible = false

  private var modelInitialized = false

  private var fMin = 1d
  private var fMax = Math.sqrt(201)
  private var legendSlope = 1d

  private var currentFieldParameters: Option[FieldParameters] = None

  val workbenchInteractor = new WorkbenchInteractor
  val menuInteractor = new MenuInteractor
  val settingsInteractor = new SettingsInteractor

  private var colorMapMode = ColorMapMode.Discrete


  {
    menuInteractor.subscribe(workbenchInteractor)
  }



  //  *********************** Workbench Interactor ***********************
  class WorkbenchInteractor extends IWorkbenchInteractor with ILayerVisibilityObserver {

    private var presenter: Option[IWorkbenchPresenter] = None

    override def functionValue(fieldPoint: Point2D): Double = {
      functionValue(fieldPoint.x, fieldPoint.y)
    }

    override def functionValue(fieldX: Double, fieldY: Double): Double = {
      val domainPoint = CoordinatesMapper.toDomain(fieldX, fieldY)
      function(domainPoint.x, domainPoint.y)
    }

    override def createIsoline(level: IsoLevel): Unit = {
      customIsoLevels += level.value
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
        cellGrid = Some(MeshGenerator.generate(fieldDimensions, currentFieldParameters.get.xNodes, currentFieldParameters.get.yNodes, function, CoordinatesMapper))
        CoordinatesMapper.setMapping(fieldDimensions, function.domain.get)
        IsolinesController.clearMapped()
        IsolinesController.mapToFieldIsolines(CoordinatesMapper)
        updateField()
      }
    }

    override def setPresenter(presenter: IWorkbenchPresenter): Unit = {
      this.presenter = Some(presenter)
    }

    override def onGridVisibilityChanged(visible: Boolean): Unit = {
      if (visible)
        presenter.get.redrawGrid(cellGrid.get.cellWidth, cellGrid.get.cellHeight)
      presenter.get.setShowGrid(visible)
    }

    override def onIntersectionPointsVisibilityChanged(visible: Boolean): Unit = {
      if (visible)
        presenter.get.redrawIntersectionPoints(IsolinesController.mappedIsolines)
      presenter.get.setShowIntersectionPoints(visible)
    }

    override def onIsolinesVisibilityChanged(visible: Boolean): Unit = {
      if (visible)
        presenter.get.redrawIsolines(IsolinesController.mappedIsolines)
      presenter.get.setShowIsolines(visible)
    }

    override def onColorMapVisibilityChanged(visible: Boolean): Unit = {
      if (colorMapVisible) {
        presenter.get.redrawColorMap(colorMapMode)
      }
      redrawLegend()
      presenter.get.setShowColorMap(visible)
    }

    def updateField(): Unit = {
      if (isolinesVisible)
        presenter.get.redrawIsolines(IsolinesController.mappedIsolines)

      if (gridVisible)
        presenter.get.redrawGrid(cellGrid.get.cellWidth, cellGrid.get.cellHeight)

      if (intersectionsVisible)
        presenter.get.redrawIntersectionPoints(IsolinesController.mappedIsolines)

      if (colorMapVisible) {
        presenter.get.redrawColorMap(colorMapMode)
      }

      redrawLegend()
    }

    private def redrawLegend(): Unit = {
      if (modelInitialized) {
        presenter.get.redrawLegend(colorMapMode)

        val ticksParams = isoLevels.get.map { level =>
          ((level - fMin) / legendSlope, level)
        }

        presenter.get.redrawLegendTicks(Seq((0, fMin)))
        presenter.get.redrawLegendTicks(ticksParams)
        presenter.get.redrawLegendTicks(Seq(((fMax - fMin) / legendSlope, fMax)))
      }
    }

    def drawColorMap(): Unit = {
      presenter.get.redrawColorMap(colorMapMode)
      redrawLegend()
    }

    override def colorForValue(functionValue: Double): Color = {
      val settings = menuInteractor.settings.get
      val targetColor = isoLevels.get.indexWhere(level => functionValue < level) match {
        case number if number > -1 => settings.legendColors(number)
        case number if number == -1 => settings.legendColors.last
      }
      targetColor
    }

    override def interpolatedColorForValue(functionValue: Double): Color = {
      val settings = menuInteractor.settings.get
      val levels = isoLevels.get
      val step = (levels(1) - levels.head) / 2

      def interpolateArgbColor(functionValue: Double, lower: Double, upper: Double, colors: (Color, Color)): Color = {
        val (a1, r1, g1, b1) = ColorHelpers.colorFragments(colors._1.color)
        val (a2, r2, g2, b2) = ColorHelpers.colorFragments(colors._2.color)

        val diff = upper - lower
        val lowerC = (functionValue - lower) / diff
        val upperC = (upper - functionValue) / diff

        def interpolate(lowerColor: Int, upperColor: Int): Int = (lowerC * upperColor + upperC * lowerColor).toInt

        val alpha = interpolate(a1, a2)
        val red = interpolate(r1, r2)
        val green = interpolate(g1, g2)
        val blue = interpolate(b1, b2)
        Color(ColorHelpers.intArgb(alpha, red, green, blue))
      }


      def f(level: Double): Color = {
        val index = isoLevels.get.indexOf(level) match {
          case number if number != -1 => number
          case _ => settings.legendColors.length-1
        }
        if (functionValue <= level - step) {
          val colors = (
            settings.legendColors((index - 1).max(0)),
            settings.legendColors(index)
          )
          interpolateArgbColor(functionValue, (level - 3 * step).max(fMin), level - step, colors)
        } else {
          val colors = (
            settings.legendColors(index),
            settings.legendColors((index + 1).min(settings.legendColors.length - 1))
          )
          interpolateArgbColor(functionValue, level - step, (level + step).min(fMax), colors)
        }
      }

      isoLevels.get.find(level => functionValue < level) match {
        case Some(level) => f(level)
        case None => f(fMax)
      }
    }

    override def domainPoint(x: Double, y: Double): Option[(Double, Double)] = {
      if (modelInitialized) {
        val point = CoordinatesMapper.toDomain(x, y)
        Some((point.x, point.y))
      } else {
        None
      }
    }

    override def legendFunctionValue(x: Double, y: Double): Double = {
      legendSlope * x + fMin
    }

    override def handleLegendResize(legendWidth: Double): Unit = {
      legendSlope = (fMax - fMin) / legendWidth
      redrawLegend()
    }

  }



//  *********************** Menu Interactor ***********************
  class MenuInteractor extends IMenuInteractor with ILayerVisibilityProvider {

    private var presenter: Option[IMenuPresenter] = None

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

    override def toggleColorMapDisplay(): Unit = {
      colorMapVisible = !colorMapVisible
      subscribers.foreach(s => s.onColorMapVisibilityChanged(colorMapVisible))
    }

    override def beforeExit(): Unit = {
      println("beforeExit() invoked")
    }

    override def openModel(filePath: String): Unit = {
      Try(ConfigReader.read(filePath)) match {
        case Success(newSettings) =>
          modelInitialized = true
          _settings = Some(newSettings)
          reloadWithParameters(FieldParameters(
            newSettings.xNodes,
            newSettings.yNodes,
            -10, 10, -10, 10
          ))
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

    override def subscribe(visibilityObserver: ILayerVisibilityObserver): Unit = {
      subscribers.add(visibilityObserver)
    }

    override def unsubscribe(visibilityObserver: ILayerVisibilityObserver): Unit = {
      subscribers.remove(visibilityObserver)
    }

    override def showDiscreteColorMap(): Unit = {
      if (colorMapVisible) {
        colorMapMode = ColorMapMode.Discrete
        workbenchInteractor.drawColorMap()
      }
    }

    override def showInterpolatedColorMap(): Unit = {
      if (colorMapVisible) {
        colorMapMode = ColorMapMode.Interpolated
        workbenchInteractor.drawColorMap()
      }
    }

  }

  def reloadWithParameters(fieldParameters: FieldParameters): Unit = {
    currentFieldParameters = Some(fieldParameters)

    function.domain = FiniteDomain2D(
      fieldParameters.lowerXBound,
      fieldParameters.upperXBound,
      fieldParameters.lowerYBound,
      fieldParameters.upperYBound
    )

    CoordinatesMapper.setMapping(currentFieldDimensions, function.domain.get)

    cellGrid = Some(
      MeshGenerator.generate(
        currentFieldDimensions,
        fieldParameters.xNodes,
        fieldParameters.yNodes,
        function,
        CoordinatesMapper
      )
    )

    fMin = function.min
    fMax = function.max

    legendSlope = (fMax - fMin) / currentFieldDimensions.width

    isoLevels = Some(IsolinesController.calculateIsoLevels(fMin, fMax, _settings.get.levels))

    IsolinesController.clearAll()
    cellGrid.get.grid.foreach { cell =>
      IsolinesController.buildRawIsolines(cell, isoLevels.get)
    }

    cellGrid.get.grid.foreach { cell =>
      IsolinesController.buildRawIsolines(cell, customIsoLevels.filter(level => fMin <= level && level <= fMax))
    }

    IsolinesController.mapToFieldIsolines(CoordinatesMapper)

    workbenchInteractor.updateField()
  }




  //  *********************** Settings Interactor ***********************
  class SettingsInteractor extends ISettingsInteractor {

    private var presenter: Option[ISettingsPresenter] = None

    override def setPresenter(presenter: ISettingsPresenter): Unit = {
      this.presenter = Some(presenter)
    }

    override def applyParameters(parameters: FieldParameters): Unit = {
      reloadWithParameters(parameters.copy())
    }

    override def currentParameters: Option[FieldParameters] = currentFieldParameters

  }

}
