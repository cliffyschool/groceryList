package groceryList.parse.util

import org.apache.commons.math.fraction.FractionFormat

import scala.util.Try

object StringUtils {

  val fractionFormat = new FractionFormat()

  implicit class StringImprovements(val s: String) {
    import scala.util.control.Exception._
    def toDoubleOpt = catching(classOf[NumberFormatException]) opt s.toDouble
    def isNullOrEmpty = s == null || s.isEmpty
    def couldBeRatio = s.contains("/")
    def asRatio = {
      val numericChunks = s.split("\\s+").map(_.trim).filterNot(_.isEmpty)
      val ratioAsStr = numericChunks.last
      Try[Double](fractionFormat.parse(ratioAsStr).doubleValue())
        .toOption match {
        case Some(r) =>
          val wholeNumber = numericChunks.secondToLast
            .flatMap(s => Some(Integer.parseInt(s)))
            .getOrElse(0)
          Some(wholeNumber + r)
        case None => None
      }
    }
  }

  implicit class IterableImprovements[T](val s: Array[T]){
    def secondToLast = if (s.size > 1) Some(s(s.size - 2)) else None
  }
}
