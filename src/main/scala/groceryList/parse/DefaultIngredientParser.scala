package groceryList.parse

import groceryList.model.{ListItem, Ingredient, UnitOfMeasure}
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

  def mergeIngredients(ingredients: Ingredient*) : Seq[ListItem] = {
    ingredients.groupBy { (ingredient) => (ingredient.name, ingredient.unit)}
      .map(entry => ListItem(
      entry._1._1,
      entry._2.map(ing => ing.amount).flatten.sum match { case 0 => None case d => Some(d)},
      entry._1._2,
      entry._2))
      .toSeq
  }
}





