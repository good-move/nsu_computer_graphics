package data_layer.graphics

case class Color(alpha: Int, red: Int, green: Int, blue: Int) {
  import utils.IntExtension._

  private val componentsValid = {
    val min = Color.MinValue
    val max = Color.MaxValue

    alpha.between(min, max) &&
    red.between(min, max) &&
    green.between(min, max) &&
    blue.between(min, max)
  }

  if (!componentsValid) {
    throw new IllegalArgumentException(s"All color components must be within range [${Color.MinValue}, ${Color.MaxValue}]")
  }



}

object Color {

  private val DefaultAlpha = 255
  private val MinValue= 0
  private val MaxValue= 255

  def apply(red: Int, green: Int, blue: Int): Color = Color(DefaultAlpha, red, green, blue)

  def red(red: Int = MaxValue): Color = Color(red, 0, 0)
  def green(green: Int = MaxValue): Color = Color(0, green, 0)
  def blue(blue: Int = MaxValue): Color = Color(0 , 0, blue)

}
