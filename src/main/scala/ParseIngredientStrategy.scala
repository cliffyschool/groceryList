import org.apache.commons.math.fraction.FractionFormat
import scala.util.Try

/**
 * Created by cfreeman on 7/8/14.
 * strategies:
 * -assume <quantity> <unit> <ingredient>, check unit
 * -find known unit, derive quantity, assume rest is ingredient
 * -assume first number is quantity, assume rest is ingredient, assume no unit
 *
 */
object ParseIngredientStrategy {

  val numberOrRatio = "(a|([0-9]+\\s)?([0-9]+)/([0-9]+)|[0-9]?(\\.[0-9]+)?)".r
  val knownUnits = KnownUnits(Seq(
    ("cup", Seq("cups", "c.")),
    ("tablespoon", Seq("tbsp", "tbsp.", "tablespoons")),
    ("teaspoon", Seq("tsp", "tsp.", "teaspoons")),
    ("ounce", Seq("oz", "oz.", "ounces")),
    ("pinch", Seq("pinches")),
    ("pound", Seq("pounds", "lbs", "lbs.", "lb.")),
    ("can", Seq("cans")),
    ("stick", Seq("sticks")),
    ("jar", Seq("jars"))
  ))

  val fractionFormat = new FractionFormat()


  def getAmount(amount: String): Option[Double] = {

    amount match {
      case "a" => Some(1)
      case s: String if s.contains("/") =>
        val split = s.split("\\s+").map(_.trim).filter(_.length > 0)
        val ratio = Try[Double](fractionFormat.parse(split(split.length-1)).doubleValue()).getOrElse(-1.0)
        val wholeNumber = if (split.length > 1) Integer.parseInt(split(split.length -2)) else 0
        Some(wholeNumber + ratio)
      case s: String => Some(s.toDouble)
      case _ => None
    }
  }

  def matchKnownUnit(unitOption: Option[String]): Option[Unit] = {
    unitOption match {
      case Some(unit) =>
        knownUnits.find(unit) match {
          case Some(known) => Some(known)
          case None => Some(Unit(unit, false))
        }
      case _ => None
    }
  }

  def matchKnownUnit(unitOption: String): Option[Unit] = matchKnownUnit(unitOption match { case null => None case s: String => Some(s)})

  val assumeEasyFormat: (Option[String]) => Option[Ingredient] = {
    _ match {
      case Some(line) =>
        line.split(" ") match {
          case Array(amount, unitString, "of", ingredient) if amount.matches(numberOrRatio.toString) =>
            println("of candidate")
            buildKnownUnitIngredient(amount, unitString, ingredient)
          case Array(amount, unitString, ingredient)  if amount.matches(numberOrRatio.toString) =>
            buildKnownUnitIngredient(amount, unitString, ingredient)
          case Array(amount, _*) =>
            println("Generic")
            None
          case _ => None
        }
      case None => None
    }
  }

  def buildKnownUnitIngredient(amountString: String, unitMaybe: String, name: String) = matchKnownUnit(unitMaybe) match {
    case Some(unit) if unit.known =>
      Some(Ingredient(name, getAmount(amountString), Some(unit)))
    case _ => None
  }

  val ingredientWithNumericContentPattern = "([0-9/\\.]+) ([0-9/\\. ]+[ -]{1}[\\w]+\\.? [\\w]+) ([\\w\\s]+)".r
  val assumeIngredientContainsNumbers: (Option[String]) => Option[Ingredient] = {
    for {
      line <- _
      knownUnit <- detectKnownUnits(line).headOption
      unitQuantity <- findFirstNumberBefore(line, knownUnit._2)
      firstNumMatch <- numberOrRatio.findFirstMatchIn(line)
      firstNumOverall <- getAmount(firstNumMatch.matched)
      if unitQuantity._2 != firstNumMatch.start
      everythingAfterFirstNum = line.substring(firstNumMatch.end)
    } yield Ingredient(name=everythingAfterFirstNum, unit=None, amount=Some(firstNumOverall))
  }

  val assumeNoUnits: (Option[String]) => Option[Ingredient] = {
    _ match {
      case None => None
      case Some(line) =>
        numberOrRatio.findFirstMatchIn(line) match {
          case Some(num) =>
            Some(Ingredient(amount = getAmount(num.matched), unit=None, name=line.substring(num.end).trim))
          case _ => None
        }
    }
  }

  val assumeItemNameOnly: (Option[String]) => Option[Ingredient] = {
    _ match {
      case None => None
      case Some(line) => Some(Ingredient(name = line, amount = None, unit = None))
    }
  }

  val assumeKnownUnit: (Option[String]) => Option[Ingredient] = {
    for {
      line <- _
      firstMatch <- detectKnownUnits(line).headOption
      firstNum <- findFirstNumberBefore(line, firstMatch._2)
      everythingAfterUnit = line.substring(firstMatch._3).trim
    } yield Ingredient(name=everythingAfterUnit, amount = firstNum._1, unit=Some(firstMatch._1))
  }

  def findFirstNumberBefore(line: String, pos: Int) = {
    numberOrRatio.findFirstMatchIn(line) match {
      case Some(matched) if matched.start < pos => Some((getAmount(matched.matched), matched.start, matched.end))
      case _ => None
    }
  }

  val unitPatternRgx = "[\\w\\.]+".r
  val detectKnownUnits: (String) => Seq[(Unit,Int,Int)] = {
    unitPatternRgx.findAllMatchIn(_)
      .map(m => (matchKnownUnit(m.matched),m.start, m.end))
      .map{
            case (Some(knownUnit),start, end) if knownUnit.known => Some((knownUnit,start,end))
            case _ => None
      }
      .flatten
      .toSeq
  }
}
