package ru.nsu.fit.g15201.boltava.domain_layer.exception

final case class ImageInitializationError(message: String) extends RuntimeException(message)