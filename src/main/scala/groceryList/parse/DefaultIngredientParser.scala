package groceryList.parse

import scala.collection.mutable
import groceryList.model.{Ingredient, UnitOfMeasure}
import groceryList.parse.util.StringUtils._
/**
 * Created by U6017873 on 7/6/2014.
 *
 */
case class DefaultIngredientParser(strategies: Array[Strategy]) extends IngredientParser {

  def fromLine: (String) => Option[Ingredient] = {

    case line:String if line.isNullOrEmpty => None
    case line:String =>
      strategies.toStream.flatMap (s => s._2(line)).headOption
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





