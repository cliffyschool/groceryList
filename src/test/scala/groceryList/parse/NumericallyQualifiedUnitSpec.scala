package groceryList.parse

import groceryList.model.{KnownUnitOfMeasure, Ingredient, UnitOfMeasure}
import org.specs2.mutable.Specification

class NumericallyQualifiedUnitSpec extends Specification {

  def parse: (String) => Option[Ingredient] = ParseIngredientStrategy.assumeNumericallyQualifiedUnit


  "Given a line with no known units, it" should {
    "return none" in {
      parse("3 4-ab. chicken breasts") must beNone
    }
  }

  "Given a line with only 1 known unit, it" should {
    "return none" in {
      parse("6 4 oz. pork chops") must beNone
    }
  }

  val numericallyQualifiedLine = "3 10 1/2 oz cans diced tomatoes"

  s"Given '$numericallyQualifiedLine', it" should {
    val ingredient = parse(numericallyQualifiedLine)

    "return some ingredient" in {
      ingredient must beSome
    }

    "return some unit" in {
      ingredient.get.unit must beSome
    }

    "use '10 1/2 oz cans' as the unit" in {
      ingredient.get.unit.get.name must contain("10") and contain("can")
    }

    "use 3 as the quantity" in {
      ingredient.get.amount must beSome(3)
    }

    "use 'diced tomatoes' as the ingredient name" in {
      ingredient.get.name must equalTo("diced tomatoes")
    }
  }
}
