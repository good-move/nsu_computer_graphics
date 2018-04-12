package data_layer.geometry

import breeze.linalg.DenseMatrix
import data_layer.graphics.Color

case class Solid(pivot: Point3D,
                 color: Color,
                 rotationMatrix: DenseMatrix[Double],
                 spline: Seq[Point2D]
                )
