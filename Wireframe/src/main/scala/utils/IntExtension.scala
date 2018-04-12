package utils

object IntExtension {

  implicit class RichInt(val value: Int) extends AnyVal {

    def between(lower: Double, upper: Double): Boolean = {
      lower <= value && value <= upper
    }

  }

}
