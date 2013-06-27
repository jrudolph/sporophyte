package sporophyte

import java.nio.charset.Charset

trait Configuration {
  val charset = Charset.forName("utf8")
  val suffix = "42"
  val constantFactor = 23

  def printWithSuffix(str: String): Unit =
    println(str + suffix)

  def plainPrinting(str: String): Unit =
    println(str)

  val logger: String => Unit =
    if (System.getProperty("plain.printing", "true").toBoolean) plainPrinting else printWithSuffix
}
