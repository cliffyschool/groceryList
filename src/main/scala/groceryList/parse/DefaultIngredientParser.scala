package groceryList.parse

import scala.collection.mutable
import groceryList.model.{Ingredient, UnitOfMeasure}

/**
 * Created by U6017873 on 7/6/2014.
 *
 */
class DefaultIngredientParser extends IngredientParser {

  val strategies = Array[(String, (String) => Option[Ingredient])](
    "easy" -> ParseIngredientStrategy.assumeEasyFormat,
    "ingredientWithNumbers" -> ParseIngredientStrategy.assumeIngredientContainsNumbers,
    "knownUnit" -> ParseIngredientStrategy.assumeKnownUnit,
    "noUnits" -> ParseIngredientStrategy.assumeNoUnits,
    "itemNameOnly" -> ParseIngredientStrategy.assumeItemNameOnly
  )

  def fromLine(line: String) : Option[Ingredient] = {
    val strOpt = line match {
      case s if s == null || s.length < 1 => None
      case s => Some(s)
    }
    if (strOpt == None)
      return None

    for (strategy <- strategies) {
      val result = strategy._2(line)
      if (result.isDefined) {
        return result
      }
    }
    None
  }

  def makeList(l1: Seq[Ingredient], l2: Seq[Ingredient]) : Seq[Ingredient] = {
    val combined = l1 ++ l2
    val r = combined.groupBy { (ingredient) => (ingredient.name, ingredient.unit)}
      .mapValues(_.reduce { (left, right) => Ingredient(left.name, combineAmounts(left, right), left.unit)})
      .values.toList
    r
  }

  def combineAmounts(left: Ingredient, right: Ingredient): Option[Double] = {
    (left.amount, right.amount) match {
      case (Some(l), Some(r)) => Some(l + r)
      case (Some(l), None) => Some(l)
      case (None, Some(r)) => Some(r)
      case (None, None) => None
    }
  }
}

object DefaultIngredientParser {
  val p = new DefaultIngredientParser()

  def parse(line: String) = p.fromLine(line)
}






