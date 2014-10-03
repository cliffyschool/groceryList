package groceryList.parse

import groceryList.parse.util.StringUtils
import org.apache.commons.math.fraction.FractionFormat
import scala.util.Try
import StringUtils._
import groceryList.model.{UnitOfMeasure, Ingredient}

import scala.util.matching.Regex

/**
 * Created by cfreeman on 7/8/14.
 * strategies:
 * -assume <quantity> <unit> <ingredient>, check unit
 * -find known unit, derive quantity, assume rest is ingredient
 * -assume first number is quantity, assume rest is ingredient, assume no unit
 *
 */
object ParseIngredientStrategy {

  val numberOrRatio = "(^a|(\\d+\\s)?(\\d+)/(\\d+)|\\d*(\\.\\d+)?)".r
  val knownUnits = KnownUnits(Map(
    "cup" -> Seq("cups", "c."),
    "tablespoon" -> Seq("tbsp", "tbsp.", "tablespoons"),
    "teaspoon" -> Seq("tsp", "tsp.", "teaspoons"),
    "ounce" -> Seq("oz", "oz.", "ounces"),
    "pinch" -> Seq("pinches"),
    "pound" -> Seq("pounds", "lbs", "lbs.", "lb."),
    "can" -> Seq("cans"),
    "stick" -> Seq("sticks"),
    "jar" -> Seq("jars")
  ))

  def getAmount: (String) => Option[Double] = {
    case numAsStr if numAsStr.isNullOrEmpty => None
    case "a" => Some(1)
    case s: String if s.couldBeRatio => s.asRatio
    case s: String => s.toDoubleOpt
    case _ => None
  }

  def matchKnownUnit: (String) => Option[UnitOfMeasure] = {
    case unitName: String if unitName != null && !unitName.isEmpty =>
      knownUnits.find(unitName) match {
        case unit if unit.known => Some(unit)
        case _ => None
      }
    case _ => None
  }

  case class Match[T](matched: T, startOfMatch: Int, endOfMatch: Int)

  val unitPatternRgx = "[\\w\\.]+".r

  def detectKnownUnits(str:String) : Seq[Match[UnitOfMeasure]] = {
    unitPatternRgx.findAllMatchIn(str)
      .map { m =>
      matchKnownUnit(m.matched) match {
        case Some(knownUnit) => Some(Match(knownUnit, m.start, m.end))
        case _ => None
      }
    }
    .flatten
    .toSeq
  }

  val assumeEasyFormat: (String) => Option[Ingredient] = {
    case s: String if s.isNullOrEmpty => None
    case line: String =>
      line.split(" ") match {
        case Array(amount, unitString, "of", ingredient) if amount.matches(numberOrRatio.toString()) =>
          buildKnownUnitIngredient(amount, unitString, ingredient)
        case Array(amount, unitString, ingredient) if amount.matches(numberOrRatio.toString()) =>
          buildKnownUnitIngredient(amount, unitString, ingredient)
        case _ => None
      }
  }

  def buildKnownUnitIngredient(amountString: String, unitMaybe: String, name: String) =
    matchKnownUnit(unitMaybe)
      .flatMap(u => Some(Ingredient(u.name, getAmount(amountString), Some(u))))


  def assumeIngredientContainsNumbers (line:String) : Option[Ingredient] = {
    for {
      knownUnits <- Option(detectKnownUnits(line))
      if knownUnits.length >= 2
      qualifierUnit = knownUnits(0)
      mainUnit = knownUnits(1)
      qualifierQuantity:Match[Double] <- findLastNumberBefore(line, qualifierUnit.startOfMatch)
      firstNumMatch <- numberOrRatio.findFirstMatchIn(line)
      firstNumOverall <- getAmount(firstNumMatch.matched)
      if qualifierQuantity.startOfMatch != firstNumMatch.start
      qualifiedUnit = buildQualifiedUnit(qualifierQuantity.matched, qualifierUnit.matched, mainUnit.matched)
      everythingAfterMainUnit = line.substring(mainUnit.endOfMatch).trim
    } yield Ingredient(name = everythingAfterMainUnit, unit = Some(qualifiedUnit), amount = Some(firstNumOverall))
  }

  val assumeNoUnits: (String) => Option[Ingredient] = {
    case s:String if s.isNullOrEmpty => None
    case line:String =>
      numberOrRatio.findFirstMatchIn(line)
        .flatMap(rgxMatch =>Some(Ingredient(line.substring(rgxMatch.end).trim,getAmount(rgxMatch.matched),None)))
  }

  val assumeItemNameOnly: (String) => Option[Ingredient] = {
    case s:String if s.isNullOrEmpty => None
    case line:String => Some(Ingredient(line, None, None))
  }

  def assumeKnownUnit(line: String) : Option[Ingredient] = {
    for {
      firstMatch <- detectKnownUnits(line).headOption
      unitQualifier <- findLastNumberBefore(line, firstMatch.startOfMatch)
      firstNumOverall <- numberOrRatio.findFirstMatchIn(line)
      if unitQualifier.startOfMatch == firstNumOverall.start
      everythingAfterUnit = line.substring(firstMatch.endOfMatch).trim
    } yield Ingredient(name = everythingAfterUnit, amount = Some(unitQualifier.matched), unit = Some(firstMatch.matched))
  }

  def findLastNumberBefore(line: String, pos: Int) = {
    val lastNumberMaybe =
      numberOrRatio
        .findAllMatchIn(line)
        .toSeq
        .filter(m => m.matched.length > 0)
        .filter(m => m.end < pos)
        .lastOption

    for {
      lastNumber <- lastNumberMaybe
      amount <- getAmount(lastNumber.matched)
    } yield Match[Double](amount, lastNumber.start, lastNumber.end)

  }


  def buildQualifiedUnit(qualifierQuantity: Double, qualifierUnit: groceryList.model.UnitOfMeasure, mainUnit: groceryList.model.UnitOfMeasure): groceryList.model.UnitOfMeasure = {
    val compoundUnit = qualifierQuantity + " " + qualifierUnit.name + " " + mainUnit.name
    println(compoundUnit)
    UnitOfMeasure(known = false, name = compoundUnit)
  }
}
