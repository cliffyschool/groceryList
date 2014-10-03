package groceryList.parse

import groceryList.model.{Ingredient, UnitOfMeasure}
import org.specs2.mutable.Specification

/**
 * Created by U6017873 on 7/27/2014.
 */
class IngredientContainingNumbersSpec extends Specification {

  "ingredientContainingNumbers strategy " should {

    def strategy: (String) => Option[Ingredient] = ParseIngredientStrategy.assumeIngredientContainsNumbers

    "return None if no known units are found" in {
      val i = strategy("3 4-ab. chicken breasts")
      i must beNone
    }

    "return None if only 1 known unit is found" in {
      val i = strategy("6 4 oz. pork chops")
      i must beNone
    }

    "extract the first known unit" in {
      val i = strategy("3 10 1/2 oz cans diced tomatoes")
      val maybeUnit =
        i match {
          case Some(Ingredient(_, _, unit)) => unit
          case _ => None
        }
      maybeUnit must beSome[UnitOfMeasure]
      maybeUnit.get.name must equalTo("10.5 ounce can")
    }

    "extract the first known unit (as decimal)" in {
      val i = strategy("3 10.5 oz. cans diced tomatoes")
      val maybeUnit =
        i match {
          case Some(Ingredient(_, _, unit)) => unit
          case _ => None
        }
      maybeUnit must beSome[UnitOfMeasure]
      maybeUnit.get.name must equalTo("10.5 ounce can")
    }

    "extract the first number as the quantity" in {
      val i = strategy("3 10 1/2 oz cans diced tomatoes")
      val quantity = i match {
        case Some(Ingredient(_, Some(q), _)) => q
        case _ => -1
      }
      quantity must equalTo(3.0)
    }

    "extract the ingredient name" in {
      val i = strategy("3 10 1/2 oz cans diced tomatoes")
      val name = i match {
        case Some(Ingredient(name, _, _)) => name
        case _ => ""
      }
      name must equalTo("diced tomatoes")
    }

  }

}
