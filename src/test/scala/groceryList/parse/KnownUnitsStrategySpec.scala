package groceryList.parse

import groceryList.model.{KnownUnitOfMeasure, Ingredient, UnitOfMeasure}
import org.specs2.mutable.Specification

/**
 * Created by U6017873 on 7/13/2014.
 */
class KnownUnitsStrategySpec extends Specification {

  def beAKnownUnit = beSome[KnownUnitOfMeasure]

  def strategy: (String) => Option[Ingredient] = ParseIngredientStrategy.assumeKnownUnit

  "Given a line containing a known unit anywhere after a number, it" should {
    val line = "1 gibberish hey there cup 1.4 blah blee"
    val ingredient = strategy(line)

    "return some ingredient" in {
      ingredient must beSome[Ingredient]
    }

    "detect the known unit" in {
      ingredient.get.unit must beSome(KnownUnitOfMeasure("cup"))
    }
  }

  "Given a line starting with one integer, it" should {
    val line = "1 cup butter"
    val ingredient = strategy(line)

    "return some ingredient" in {
      ingredient must beSome[Ingredient]
    }

    "use that integer as quantity" in {
      ingredient.get.amount must beSome(1)
    }
  }

  "Given a line starting with a ratio, it" should {
    val line = "1 1/2 cups butter"
    val ingredient = strategy(line)

    "return some ingredient" in {
      ingredient must beSome[Ingredient]
    }

    "use that ratio as quantity" in {
      ingredient.get.amount must beSome(1.5)
    }
  }

  "Given a line starting with a decimal, it" should {
    val line = "1.53 cups butter"
    val ingredient = strategy(line)

    "return some ingredient" in {
      ingredient must beSome[Ingredient]
    }

    "use that ratio as quantity" in {
      ingredient.get.amount must beSome(1.53)
    }
  }

  "known units strategy" should {

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
