package groceryList.parse.strategies.unitWithNumber

import groceryList.parse.ParseIngredientStrategy
import groceryList.parse.strategies.ParseStrategyTest
import org.specs2.mutable.Specification

class NumericallyQualifiedUnitSpec extends Specification with ParseStrategyTest {

  override def parse = ParseIngredientStrategy.numericallyQualifiedUnit

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

}
