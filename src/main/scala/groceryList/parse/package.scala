package groceryList

import groceryList.model.Ingredient

/**
 * Created by cfreeman on 10/4/14.
 */
package object parse {
  type Strategy = (String, (String) => Option[Ingredient])
}
