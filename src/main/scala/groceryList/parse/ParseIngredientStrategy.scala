package groceryList.parse

import org.apache.commons.math.fraction.FractionFormat
import scala.util.Try
import groceryList.model.{UnitOfMeasure, Ingredient}

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
    "ounce" ->  Seq("oz", "oz.", "ounces"),
    "pinch" ->  Seq("pinches"),
    "pound" ->  Seq("pounds", "lbs", "lbs.", "lb."),
    "can" ->  Seq("cans"),
    "stick" ->  Seq("sticks"),
    "jar" ->  Seq("jars")
  ))

  val fractionFormat = new FractionFormat()


  def getAmount:(String) => Option[Double] = {
    case "a" => Some(1)
    case s: String if s.contains("/") =>
      val split = s.split("\\s+").map(_.trim).filterNot(_.isEmpty)
      val ratio = Try[Double](fractionFormat.parse(split(split.length - 1)).doubleValue()).getOrElse(-1.0)
      val wholeNumber = if (split.length > 1) Integer.parseInt(split(split.length - 2)) else 0
      Some(wholeNumber + ratio)
    case s: String if s.length > 0 => Some(s.toDouble)
    case _ => None
  }

  def matchKnownUnit:(String) => Option[UnitOfMeasure] = {
    case unitName:String if unitName != null && !unitName.isEmpty =>
      knownUnits.find(unitName) match {
        case None => Some(UnitOfMeasure(name = unitName, known = false))
        case o:Option[UnitOfMeasure] => o
      }
    case _ => None
  }

  val unitPatternRgx = "[\\w\\.]+".r
  val detectKnownUnits: (String) => Seq[(groceryList.model.UnitOfMeasure, Int, Int)] = {
    unitPatternRgx.findAllMatchIn(_)
      .map(m => (matchKnownUnit(m.matched), m.start, m.end))
      .map {
      case (Some(knownUnit), start, end) if knownUnit.known => Some((knownUnit, start, end))
      case _ => None
    }
      .flatten
      .toSeq
  }

  val assumeEasyFormat: (Option[String]) => Option[Ingredient] = {
    case Some(line) =>
      line.split(" ") match {
        case Array(amount, unitString, "of", ingredient) if amount.matches(numberOrRatio.toString()) =>
          println("of candidate")
          buildKnownUnitIngredient(amount, unitString, ingredient)
        case Array(amount, unitString, ingredient) if amount.matches(numberOrRatio.toString()) =>
          buildKnownUnitIngredient(amount, unitString, ingredient)
        case Array(amount, _*) =>
          println("Generic")
          None
        case _ => None
      }
    case None => None
  }

  def buildKnownUnitIngredient(amountString: String, unitMaybe: String, name: String) = matchKnownUnit(unitMaybe) match {
    case Some(unit) if unit.known =>
      Some(Ingredient(name, getAmount(amountString), Some(unit)))
    case _ => None
  }

  val assumeIngredientContainsNumbers: (Option[String]) => Option[Ingredient] = {
    for {
      line: String <- _
      knownUnits = detectKnownUnits(line)
      if knownUnits.length >= 2
      qualifierUnit = knownUnits(0)
      mainUnit = knownUnits(1)
      qualifierQuantityMaybe <- findLastNumberBefore(line, qualifierUnit._2)
      qualifierQuantity <- qualifierQuantityMaybe
      firstNumMatch <- numberOrRatio.findFirstMatchIn(line)
      firstNumOverall <- getAmount(firstNumMatch.matched)
      if qualifierQuantity._2 != firstNumMatch.start
      qualifiedUnit = buildQualifiedUnit(qualifierQuantity._1, qualifierUnit._1, mainUnit._1)
      everythingAfterMainUnit = line.substring(mainUnit._3).trim
    } yield Ingredient(name = everythingAfterMainUnit, unit = Some(qualifiedUnit), amount = Some(firstNumOverall))
  }

  val assumeNoUnits: (Option[String]) => Option[Ingredient] = {
    case None => None
    case Some(line) =>
      numberOrRatio.findFirstMatchIn(line) match {
        case Some(num) =>
          println("nounits: matched: " + num.matched)
          Some(Ingredient(amount = getAmount(num.matched), unit = None, name = line.substring(num.end).trim))
        case _ => None
      }
  }

  val assumeItemNameOnly: (Option[String]) => Option[Ingredient] = {
    case None => None
    case Some(line) => Some(Ingredient(name = line, amount = None, unit = None))
  }

  val assumeKnownUnit: (Option[String]) => Option[Ingredient] = {
    for {
      line <- _
      firstMatch <- detectKnownUnits(line).headOption
      unitQualifierMaybe <- findLastNumberBefore(line, firstMatch._2)
      unitQualifier <- unitQualifierMaybe
      firstNumOverall <- numberOrRatio.findFirstMatchIn(line)
      if unitQualifier._2 == firstNumOverall.start
      everythingAfterUnit = line.substring(firstMatch._3).trim
    } yield Ingredient(name = everythingAfterUnit, amount = Some(unitQualifier._1), unit = Some(firstMatch._1))
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
    } yield Some((amount, lastNumber.start, lastNumber.end))

  }


  def buildQualifiedUnit(qualifierQuantity: Double, qualifierUnit: groceryList.model.UnitOfMeasure, mainUnit: groceryList.model.UnitOfMeasure): groceryList.model.UnitOfMeasure = {
    val compoundUnit = qualifierQuantity + " " + qualifierUnit.name + " " + mainUnit.name
    println(compoundUnit)
    UnitOfMeasure(known = false, name = compoundUnit)
  }
}
