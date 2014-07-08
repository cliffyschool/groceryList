import org.specs2.matcher.DataTables
import org.specs2.Specification

/**
 * Created by U6017873 on 7/7/2014.
 */
class ParseUnitsSpec extends Specification with DataTables{ def is =
  "tests for parsing ingredients"  ! e1

  val salt = "salt"
  val parser = new Ingredients

  def e1 =
    "line"   | "expectedAmount" | "expectedUnit" | "expectedIngredient" |
      "1 cup salt"  !! 1 ! "cups" ! salt |
      "1 cups of salt"  !! 1  ! "cups" ! salt |
      "1 c. of salt"  !! 1  ! "cups" ! salt |
      "1 tablespoon salt"  !! 1  ! "tablespoons" ! salt |
      "1 tbsp salt"  !! 1  ! "tablespoons" ! salt |
      "1 tbsp. salt"  !! 1  ! "tablespoons" ! salt |
      "1 teaspoon salt"  !! 1  ! "teaspoons" ! salt |
      "1 teaspoons salt"  !! 1  ! "teaspoons" ! salt |
      "1 tsp salt"  !! 1  ! "teaspoons" ! salt |
      "1 tsp. salt"  !! 1  ! "teaspoons" ! salt |
      "1 ounces salt"  !! 1  ! "ounces" ! salt |
      "1 oz salt"  !! 1  ! "ounces" ! salt |
      "1 oz. salt"  !! 1  ! "ounces" ! salt |>{
      (line,expectedAmount,expectedUnit,expectedIngredient) => {parser.fromLine(line) must beSome(Ingredient(name = expectedIngredient, amount=expectedAmount, unit=expectedUnit))}
    }
}
