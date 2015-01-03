package domain.line

import commons.StringUtils
import commons.StringUtils._
import domain.{UnknownUnitOfMeasure, WellKnownUnitOfMeasure}


object LineParserStrategy {

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

  def simpleFormat: (String) => Option[Line] = {
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
      case Some(knownUnit) => Some(Line(ingredient, parseAmount(amount), Some(knownUnit)))
      case None => Some(Line(ingredient, parseAmount(amount), Some(UnknownUnitOfMeasure(unit))))
    }
  }

  def numericallyQualifiedUnit (line:String) : Option[Line] = {
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
    } yield Line(name = everythingAfterMainUnit, unit = Some(qualifiedUnit), amount = Some(firstNumOverall))
  }

  def noUnits(line : String): Option[Line] = {
      numberOrRatioRegex.findFirstMatchIn(line)
        .flatMap(rgxMatch =>Some(Line(  line.substring(rgxMatch.end).trim,
                                              parseAmount(rgxMatch.matched),
                                              None)))
  }

  def itemNameOnly: (String) => Option[Line] = {
    case s:String if s.isNullOrEmpty => None
    case line:String => Some(Line(line, None, None))
  }

  def knownUnit(line: String) : Option[Line] = {
    for {
      firstMatch <- detectKnownUnits(line).headOption
      unitQualifier <- findLastNumberBefore(line, firstMatch.startOfMatch)
      firstNumOverall <- numberOrRatioRegex.findFirstMatchIn(line)
      if unitQualifier.startOfMatch == firstNumOverall.start
      everythingAfterUnit = line.substring(firstMatch.endOfMatch).trim
      if everythingAfterUnit.length > 0
    } yield
      Line(everythingAfterUnit, Some(unitQualifier.matched), Some(firstMatch.matched))
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
