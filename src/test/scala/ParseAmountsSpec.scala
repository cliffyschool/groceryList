import org.specs2.matcher.DataTables
import org.specs2.Specification

/**
 * Created by U6017873 on 7/7/2014.
 */
class ParseAmountsSpec extends Specification with DataTables{ def is =
  "tests for parsing ingredients"  ! e1

  val parser = new Ingredients

  def e1 =
    "line" | "expectedAmount" | "expectedUnit" | "expectedIngredient" |
      "2 cups butter" !! 2.0d ! "cups" ! "butter" |
      "2 cups of butter" !! 2.0d ! "cups" ! "butter" |
      "a cup of butter" !! 1.0d ! "cups" ! "butter" |
      "1 cup butter" !! 1.0d ! "cups" ! "butter" |
      "1/2 c. butter" !! 0.5d ! "cups" ! "butter" |
      ".375 c. butter" !! 0.375d ! "cups" ! "butter" |
      "2 10.5 oz cans diced tomatoes" !! 2d ! "10.5 oz cans" ! "diced tomatoes" |
      "2 10.5 oz. cans diced tomatoes" !! 2d ! "10.5 oz. cans" ! "diced tomatoes" |
      "2 10.5-oz. cans diced tomatoes" !! 2d ! "10.5-oz. cans" ! "diced tomatoes" |
      "2 10.5-oz cans diced tomatoes" !! 2d ! "10.5-oz cans" ! "diced tomatoes" |
      "2 10 1/2 oz cans diced tomatoes" !! 2d ! "10 1/2 oz cans" ! "diced tomatoes" |
      "0.375 cup butter" !! 0.375d ! "cups" ! "butter" |> {
      (line,expectedAmount,expectedUnit,expectedIngredient) => {parser.fromLine(line) must beSome(Ingredient(name = expectedIngredient, amount=expectedAmount, unit=expectedUnit))}
    }
}
