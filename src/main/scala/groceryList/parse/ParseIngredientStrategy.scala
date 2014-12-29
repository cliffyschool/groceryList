package groceryList.parse

import groceryList.parse.util.StringUtils
import org.apache.commons.math.fraction.FractionFormat
import scala.util.Try
import StringUtils._
import groceryList.model.{UnknownUnitOfMeasure, WellKnownUnitOfMeasure, UnitOfMeasure, Ingredient}

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

  val wellKnownUnitsFinder = WellKnownUnitsFinder(Map(
    "cup" -> Seq("cups", "c."),
    "tablespoon" -> Seq("tbsp", "tbsp.", "tablespoons"),
    "teaspoon" -> Seq("tsp", "tsp.", "teaspoons"),
    "ounce" -> Seq("oz", "oz.", "ounces"),
    "pinch" -> Seq("pinches"),
    "pound" -> Seq("pounds", "lbs", "lbs.", "lb."),
    "can" -> Seq("cans"),
    "stick" -> Seq("sticks"),
    "jar" -> Seq("jars"),
    "jug" -> Seq("jugs")
  ))

  def parseAmount: (String) => Option[Double] = {
    case str if str.isNullOrEmpty => None
    case "a" => Some(1)
    case s: String if s.couldBeRatio => s.asRatio
    case s: String => s.toDoubleOpt
    case _ => None
  }

  case class Match[T](matched: T, startOfMatch: Int, endOfMatch: Int)

  val unitPatternRgx = "[\\w\\.]+".r

  def detectKnownUnits(str:String) = {
    unitPatternRgx.findAllMatchIn(str)
      .map(m => wellKnownUnitsFinder.matchKnownUnit(m.matched)
      .flatMap(knownUnit => Some(Match(knownUnit, m.start, m.end))))
    .flatten
    .toSeq
  }

  def simpleFormat: (String) => Option[Ingredient] = {
    case s: String if s.isNullOrEmpty => None
    case line: String =>
      line.split(" ") match {
        case Array(amount, unitString, "of", ingredient) if amount.matches(numberOrRatioRegex.toString()) =>
          buildIngredient(ingredient, amount, unitString)
        case Array(amount, unitString, ingredient) if amount.matches(numberOrRatioRegex.toString()) =>
          buildIngredient(ingredient, amount, unitString)
        case _ => None
      }
  }

  def buildIngredient(ingredient: String, amount:String,unit:String) = {
    wellKnownUnitsFinder.matchKnownUnit(unit) match{
      case Some(knownUnit) => Some(Ingredient(ingredient, parseAmount(amount), Some(knownUnit)))
      case None => Some(Ingredient(ingredient, parseAmount(amount), Some(UnknownUnitOfMeasure(unit))))
    }
  }

  def numericallyQualifiedUnit (line:String) : Option[Ingredient] = {
    for {
      knownUnits <- Option(detectKnownUnits(line))
      if knownUnits.length >= 2
      (qualifierUnit,mainUnit) = (knownUnits(0), knownUnits(1))
      qualifierQuantity <- findLastNumberBefore(line, qualifierUnit.startOfMatch)
      firstNumMatch <- numberOrRatioRegex.findFirstMatchIn(line)
      firstNumOverall <- parseAmount(firstNumMatch.matched)
      if qualifierQuantity.startOfMatch != firstNumMatch.start
      qualifiedUnit = buildQualifiedUnit(qualifierQuantity.matched, qualifierUnit.matched, mainUnit.matched)
      everythingAfterMainUnit = line.substring(mainUnit.endOfMatch).trim
    } yield Ingredient(name = everythingAfterMainUnit, unit = Some(qualifiedUnit), amount = Some(firstNumOverall))
  }

  def noUnits(line : String): Option[Ingredient] = {
      numberOrRatioRegex.findFirstMatchIn(line)
        .flatMap(rgxMatch =>Some(Ingredient(  line.substring(rgxMatch.end).trim,
                                              parseAmount(rgxMatch.matched),
                                              None)))
  }

  def itemNameOnly: (String) => Option[Ingredient] = {
    case s:String if s.isNullOrEmpty => None
    case line:String => Some(Ingredient(line, None, None))
  }

  def knownUnit(line: String) : Option[Ingredient] = {
    for {
      firstMatch <- detectKnownUnits(line).headOption
      unitQualifier <- findLastNumberBefore(line, firstMatch.startOfMatch)
      firstNumOverall <- numberOrRatioRegex.findFirstMatchIn(line)
      if unitQualifier.startOfMatch == firstNumOverall.start
      everythingAfterUnit = line.substring(firstMatch.endOfMatch).trim
      if everythingAfterUnit.length > 0
    } yield
      Ingredient(everythingAfterUnit, Some(unitQualifier.matched), Some(firstMatch.matched))
  }

  def findLastNumberBefore(line: String, pos: Int) = {
    val lastNumberMaybe = numberOrRatioRegex
        .findAllMatchIn(line)
        .toSeq
        .filter(m => m.matched.length > 0 && m.end < pos)
        .lastOption

    for {
      lastNumber <- lastNumberMaybe
      amount <- parseAmount(lastNumber.matched)
    } yield Match[Double](amount, lastNumber.start, lastNumber.end)

  }


  def buildQualifiedUnit(qualifierQuantity: Double,
                         qualifierUnit: WellKnownUnitOfMeasure,
                         mainUnit: WellKnownUnitOfMeasure) = {
    val compoundUnit = qualifierQuantity + " " + qualifierUnit.name + " " + mainUnit.name
    UnknownUnitOfMeasure(compoundUnit)
  }
}
