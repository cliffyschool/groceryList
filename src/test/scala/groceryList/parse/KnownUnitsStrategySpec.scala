package groceryList.parse

import groceryList.model.{KnownUnitOfMeasure, Ingredient, UnitOfMeasure}
import org.specs2.mutable.Specification

/**
 * Created by U6017873 on 7/13/2014.
 */
class KnownUnitsStrategySpec extends Specification {

  def beAKnownUnit = beSome[KnownUnitOfMeasure]

  "known units strategy" should {

    "use the first number as quantity" in {
      val u = ParseIngredientStrategy.assumeKnownUnit("1 cup butter")
      u must beSome(Ingredient(amount=Some(1),unit=Some(KnownUnitOfMeasure("cup")), name="butter"))
    }

    "handle ratios as quantity" in {
      val u = ParseIngredientStrategy.assumeKnownUnit("1 1/2 cups butter")
      u must beSome(Ingredient(amount=Some(1.5),unit=Some(KnownUnitOfMeasure("cup")), name="butter"))
    }

    "handle decimal values as quantity" in {
      val u = ParseIngredientStrategy.assumeKnownUnit("1.53 cups butter")
      u must beSome(Ingredient(amount=Some(1.53),unit=Some(KnownUnitOfMeasure("cup")), name="butter"))
    }

    "return None if the amount qualifying the unit is not the first number" in {
      val u = ParseIngredientStrategy.assumeKnownUnit("6 8 oz. steaks")
      u must beNone
    }

    "ignore non-numbers between the first number and the first unit" in {
      val u = ParseIngredientStrategy.assumeKnownUnit("4 tra lee, tra la cup butter")
      u must beSome(Ingredient(amount=Some(4),unit=Some(KnownUnitOfMeasure("cup")), name="butter"))
     }

    "use the first matched unit as the unit" in {
      val u = ParseIngredientStrategy.assumeKnownUnit("1 cup tablespoon butter")
      u must beSome(Ingredient(amount=Some(1),unit=Some(KnownUnitOfMeasure("cup")), name="tablespoon butter"))
    }

    "return None if no known unit is detected" in {
      val u = ParseIngredientStrategy.assumeKnownUnit("1 cupz butter")
      u must beNone
    }

    "handle empty ingredient name" in {
      val u = ParseIngredientStrategy.assumeKnownUnit("3 cups")
      u must beSome(Ingredient(name="", amount = Some(3), unit = Some(KnownUnitOfMeasure("cup"))))
    }
  }

}
