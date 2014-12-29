package groceryList.parse

import groceryList.model.Ingredient

/**
 * Created by cfreeman on 10/4/14.
 */
trait StrategyComponent {

  val strategies = Array[(String, (String) => Option[Ingredient])](
    "easy" -> ParseIngredientStrategy.simpleFormat,
    "ingredientWithNumbers" -> ParseIngredientStrategy.numericallyQualifiedUnit,
    "knownUnit" -> ParseIngredientStrategy.knownUnit,
    "noUnits" -> ParseIngredientStrategy.noUnits,
    "itemNameOnly" -> ParseIngredientStrategy.itemNameOnly
  )
  val parser = new DefaultIngredientParser(strategies)
}
