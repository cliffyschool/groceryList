package groceryList.parse.strategies.simpleFormat

import groceryList.model.{Ingredient, UnknownUnitOfMeasure}
import groceryList.parse.strategies.ParseStrategyTest
import groceryList.parse.{ParseIngredientStrategy, StrategyComponent}
import org.specs2.matcher.DataTables
import org.specs2.mutable.Specification


class GivenSimpleFormatExamples extends Specification
with DataTables
with ParseStrategyTest
with StrategyComponent { override def is =

  "tests for parsing simple format"  ! examples

  override def parse = ParseIngredientStrategy.simpleFormat

  def examples =
    "line" | "expectedAmount" | "expectedUnit" | "expectedName" |
    "4 dips butter" !! Some(4.0d) ! Some(UnknownUnitOfMeasure("dips")) ! "butter" |
    "3.75 dips of butter" !! Some(3.75d) ! Some(UnknownUnitOfMeasure("dips")) ! "butter" |> {

      (line, expectedAmount, expectedUnit, expectedName) => {
        val expected = Ingredient(expectedName, expectedAmount, expectedUnit)
        parse(line) must beSome(expected)
      }
    }
}
