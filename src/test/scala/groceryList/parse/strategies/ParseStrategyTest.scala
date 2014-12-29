package groceryList.parse.strategies

import groceryList.model.Ingredient

/**
 * Created by cfreeman on 12/29/14.
 */
trait ParseStrategyTest {
  def parse: (String) => Option[Ingredient]
}
