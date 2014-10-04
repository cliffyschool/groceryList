package groceryList.parse

import groceryList.model.{KnownUnitOfMeasure, UnknownUnitOfMeasure, Ingredient, UnitOfMeasure}
import org.specs2.matcher.DataTables
import org.specs2.Specification

/**
 * Created by U6017873 on 7/7/2014.
 */
class ParseAmountsSpec extends Specification with DataTables{ def is =
  "tests for parsing ingredients"  ! e1

  val parser = new DefaultIngredientParser

  def e1 =
    "line" | "expectedAmount" | "expectedUnit" | "unitIsKnown" | "expectedIngredient" |
      "2 cups butter" !! 2.0d ! "cup" ! true ! "butter" |
      "2 cups of butter" !! 2.0d ! "cup" ! true ! "butter" |
      "a cup of butter" !! 1.0d ! "cup" ! true ! "butter" |
      "1 cup butter" !! 1.0d ! "cup" ! true ! "butter" |
      "1/2 c. butter" !! 0.5d ! "cup" ! true ! "butter" |
      ".375 c. butter" !! 0.375d ! "cup" ! true ! "butter" |
      "4 boneless pork chops" !! 4d ! "" ! false ! "boneless pork chops" |
      "6 4 oz. boneless pork chops" !! 6d ! "" ! false ! "4 oz. boneless pork chops" |
      "a 4 oz. boneless pork chop" !! 1d ! "" ! false ! "4 oz. boneless pork chop" |
      "6 (4 oz) boneless pork chops" !! 6d ! "" ! false ! "(4 oz) boneless pork chops" |
      "rice" !! -1d ! "" ! false ! "rice" |
      "jasmine rice" !! -1d ! "" ! false ! "jasmine rice" |
      "1 chicken" !! 1d ! "" ! false ! "chicken" |
      "2 10.5 oz cans diced tomatoes" !! 2d ! "10.5 ounce can" ! false ! "diced tomatoes" |
      "2 10.5 oz. cans diced tomatoes" !! 2d ! "10.5 ounce can" ! false ! "diced tomatoes" |
      "2 10.5-oz. cans diced tomatoes" !! 2d ! "10.5 ounce can" ! false ! "diced tomatoes" |
      "2 10.5-oz cans diced tomatoes" !! 2d ! "10.5 ounce can" ! false ! "diced tomatoes" |
      "2 10 1/2 oz cans diced tomatoes" !! 2d ! "10.5 ounce can" ! false ! "diced tomatoes" |
      "0.375 cup butter" !! 0.375d ! "cup" ! true ! "butter" |> {
      (line, expectedAmount, expectedUnit, expectedUnitIsKnown, expectedIngredient) => {
        val expected = Ingredient(name = expectedIngredient,
          amount = expectedAmount match {
            case -1 => None
            case _ => Some(expectedAmount)
          },
          unit = expectedUnit match {
          case "" => None
          case s: String => if (expectedUnitIsKnown) Some(KnownUnitOfMeasure(s)) else Some(UnknownUnitOfMeasure(s))
        })
        parser.fromLine(line) must beSome(expected)
      }
    }
}
