import org.apache.commons.math.fraction.FractionFormat
import scala.util.matching.Regex

/**
 * Created by cfreeman on 7/8/14.
 * strategies:
 * -assume <quantity> <unit> <ingredient>, check unit
 * -find known unit, derive quantity, assume rest is ingredient
 * -assume first number is quantity, assume rest is ingredient, assume no unit
 *
 */
trait ParseIngredientStrategy {

  val numberOrRatio = "(a|[0-9/\\.]+)".r
  val knownUnits = Map( "cups" -> "cups", "cup" -> "cups", "c." -> "cups",
    "tablespoons"-> "tablespoons", "tbsp" -> "tablespoons", "tbsp." -> "tablespoons", "tablespoon" -> "tablespoons",
    "teaspoon" -> "teaspoons", "teaspoons" -> "teaspoons", "tsp" -> "teaspoons", "tsp." -> "teaspoons",
    "ounce" -> "ounces", "ounces" -> "ounces", "oz" -> "ounces", "oz." -> "ounces"
  )
  val fractionFormat = new FractionFormat()


  def parseIngredient (line: Option[String]) : Option[Ingredient]

  def getAmount(amount : String) : Double = {
    amount match {
      case "a" => 1
      case s:String if s.contains("/") => fractionFormat.parse(amount).doubleValue()
      case s:String => s.toDouble
    }
  }

  def matchKnownUnit(unit : Option[String]) : Option[String] = {
    unit match {
      case None => None
      case Some(unitMaybe)  =>
        knownUnits.get(unitMaybe) match {
          case Some(synonym) => Some(synonym)
          case None => None
        }
    }
  }
}

class AssumeEasyFormat extends ParseIngredientStrategy {

  override def parseIngredient(l: Option[String]): Option[Ingredient] = {
    l match {
      case None => None
      case Some(line) =>
        line.split(" ") match {
          case Array(numberOrRatio(amount), unit, ingredient) => Some(Ingredient(ingredient, getAmount(amount), matchKnownUnit(Some(unit))))
          case Array(numberOrRatio(amount), unit, "of", ingredient) => Some(Ingredient(ingredient, getAmount(amount), matchKnownUnit(Some(unit))))
        }
    }
  }
}

class AssumeIngredientContainsNumbers extends ParseIngredientStrategy {

  val ingredientWithNumericContentPattern = "([0-9/\\.]+) ([0-9/\\. ]+[ -]{1}[\\w]+\\.? [\\w]+) ([\\w\\s]+)".r

  override def parseIngredient(l: Option[String]): Option[Ingredient] = {
    l match {
      case None => None
      case Some(line) =>
        ingredientWithNumericContentPattern.findFirstMatchIn(line) match {
          case Some(sp) if sp.groupCount == 3  => {
            Some(Ingredient(sp.group(3), getAmount(sp.group(1)), matchKnownUnit(Some(sp.group(2)))))
          }
          case _ => None
        }
    }
  }
}
