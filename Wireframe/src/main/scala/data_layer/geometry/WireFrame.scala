package data_layer.geometry

import data_layer.graphics.Color

case class WireFrame(domain: Domain,
                     pivot: Point3D,
                     color: Color,
                     rotationAngles: (Double, Double),
                     spline: Seq[Point2D]
                )
