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

  "Given a line with two numbers before the unit, it" should {
    val line = "6 8 oz. steaks"
    val ingredient = strategy(line)

    "return None" in {
      ingredient must beNone
    }
  }

  "Given a line with junk between the first number and the first unit, it" should {
    val line = "4 tra lee, tra la cup butter"
    val ingredient = strategy(line)

    "return some ingredient" in {
      ingredient must beSome[Ingredient]
    }

    "use the number as quanity" in {
      ingredient.get.amount must beSome(4)
    }

    "use the text after the unit as the ingredient" in {
      ingredient.get.name must equalTo("butter")
    }

    "use the unit" in {
      ingredient.get.unit must beSome(KnownUnitOfMeasure("cup"))
    }
  }

  "Given a line with multiple known units, it" should {
    val line = "1 cup tablespoon butter"
    val ingredient = strategy(line)

    "return some ingredient" in {
      ingredient must beSome
    }

    "use the first matched unit as the unit" in {
      ingredient.get.unit must beSome(KnownUnitOfMeasure("cup"))
    }
  }

  "Given a line with no known units, it" should {
    "return none" in {
      strategy("1 cupz butter") must beNone
    }
  }

  "Given a line missing an ingredient name, it" should {
    "return none" in {
      strategy("3 cups") must beNone
    }
  }
}
