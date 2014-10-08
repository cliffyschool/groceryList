package groceryList.parse

import groceryList.model.Ingredient

/**
 * Created by cfreeman on 10/4/14.
 */
trait StrategyComponent {

  val strategies = Array[(String, (String) => Option[Ingredient])](
    "easy" -> ParseIngredientStrategy.assumeEasyFormat,
    "ingredientWithNumbers" -> ParseIngredientStrategy.assumeNumericallyQualifiedUnit,
    "knownUnit" -> ParseIngredientStrategy.assumeKnownUnit,
    "noUnits" -> ParseIngredientStrategy.assumeNoUnits,
    "itemNameOnly" -> ParseIngredientStrategy.assumeItemNameOnly
  )
  val parser = new DefaultIngredientParser(strategies)
}
