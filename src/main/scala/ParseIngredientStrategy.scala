import org.apache.commons.math.fraction.FractionFormat

/**
 * Created by cfreeman on 7/8/14.
 * strategies:
 * -assume <quantity> <unit> <ingredient>, check unit
 * -find known unit, derive quantity, assume rest is ingredient
 * -assume first number is quantity, assume rest is ingredient, assume no unit
 *
 */
object ParseIngredientStrategy {

  val numberOrRatio = "(a|[0-9/\\.]+)".r
  val knownUnits = Map("cups" -> "cups", "cup" -> "cups", "c." -> "cups",
    "tablespoons" -> "tablespoons", "tbsp" -> "tablespoons", "tbsp." -> "tablespoons", "tablespoon" -> "tablespoons",
    "teaspoon" -> "teaspoons", "teaspoons" -> "teaspoons", "tsp" -> "teaspoons", "tsp." -> "teaspoons",
    "ounce" -> "ounces", "ounces" -> "ounces", "oz" -> "ounces", "oz." -> "ounces"
  )
  val fractionFormat = new FractionFormat()


  def getAmount(amount: String): Option[Double] = {

    amount match {
      case "a" => Some(1)
      case s: String if s.contains("/") => Some(fractionFormat.parse(amount).doubleValue())
      case s: String => Some(s.toDouble)
      case _ => None
    }
  }

  def matchKnownUnit(unitOption: Option[String]): Option[Unit] = {
    unitOption match {
      case Some(unit) =>
        knownUnits.get(unit) match {
          case Some(synonym) => Some(Unit(synonym, true))
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
          case Array(numberOrRatio(amount), unitString, ingredient) =>
            buildKnownUnitIngredient(amount, unitString, ingredient)
          case Array(numberOrRatio(amount), unitString, "of", ingredient) =>
            buildKnownUnitIngredient(amount, unitString, ingredient)
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
    _ match {
      case None => None
      case Some(line) =>
        ingredientWithNumericContentPattern.findFirstMatchIn(line) match {
          case Some(sp) if sp.groupCount == 3 => {
            Some(Ingredient(sp.group(3), getAmount(sp.group(1)), matchKnownUnit(sp.group(2))))
          }
          case _ => None
        }
    }
  }

  val noUnitsRgx = s"$numberOrRatio (.*)".r
  val assumeNoUnits: (Option[String]) => Option[Ingredient] = {
    _ match {
      case None => None
      case Some(line) =>
        noUnitsRgx.findFirstMatchIn(line) match {
          case Some(sp) if sp.groupCount == 2 =>
            Some(Ingredient(amount = getAmount(sp.group(1)), unit = None, name = sp.group(2)))
          case _ => None
        }
    }
  }
}
