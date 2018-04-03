package ru.nsu.fit.g15201.boltava.presentation_layer.menu

import ru.nsu.fit.g15201.boltava.domain_layer.data.FileExtension
import ru.nsu.fit.g15201.boltava.presentation_layer.IAlertInvoker
import ru.nsu.fit.g15201.boltava.presentation_layer.base.{IBaseInteractor, IBasePresenter}

object Contract {

  trait IMenuPresenter extends IBasePresenter with IAlertInvoker {

    def onOpenModel()
    def onExit()

    def onToggleShowGrid()
    def onToggleShowIsolines()
    def onToggleIntersectionDots()
    def onShowAbout()
    def onShowHelp()

  }

  trait IMenuInteractor extends IBaseInteractor[IMenuPresenter] {

    def beforeExit()

    def openModel(filePath: String)
    def modelFileExtension: FileExtension

    def toggleIsolinesDisplay()
    def toggleGridDisplay()
    def toggleIntersectionsDisplay()
    def toggleColorMapDisplay()
    def showDiscreteColorMap()
    def showInterpolatedColorMap()

  }

}
