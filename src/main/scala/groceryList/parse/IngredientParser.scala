package groceryList.parse

import groceryList.model.Ingredient

/**
 * Created by cfreeman on 7/29/14.
 */
trait IngredientParser {
  def fromLine: (String) => Option[Ingredient]
}
