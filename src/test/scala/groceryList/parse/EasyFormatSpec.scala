package groceryList.parse

import groceryList.model.{UnknownUnitOfMeasure, Ingredient}
import org.specs2.mutable.Specification

class EasyFormatSpec extends Specification {

  def parse: (String) => Option[Ingredient] = ParseIngredientStrategy.assumeEasyFormat


  "Given a line with like '<number> <nonNumber> <nonNumber', it" should {
    val ingredient = parse("4 dips butter")

    "return some ingredient" in {
      ingredient must beSome
    }

    "use the number as quantity" in {
      ingredient.get.amount must beSome(4)
    }

    "use the first non-number as the unit" in {
      ingredient.get.unit must beSome(UnknownUnitOfMeasure("dips"))
    }

    "use the second non-number as the ingredient" in {
      ingredient.get.name must equalTo("butter")
    }
  }

  "Given a line with like '<number> <nonNumber> of <nonNumber', it" should {
    val ingredient = parse("4 dips of butter")

    "return some ingredient" in {
      ingredient must beSome
    }

    "use the number as quantity" in {
      ingredient.get.amount must beSome(4)
    }

    "use the first non-number as the unit" in {
      ingredient.get.unit must beSome(UnknownUnitOfMeasure("dips"))
    }

    "use the second non-number as the ingredient" in {
      ingredient.get.name must equalTo("butter")
    }
  }
}
