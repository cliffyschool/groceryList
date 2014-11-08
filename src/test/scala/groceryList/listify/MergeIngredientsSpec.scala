package groceryList.listify

import groceryList.actors.Core
import groceryList.model.{WellKnownUnitOfMeasure, Ingredient}
import groceryList.parse.{StrategyComponent, DefaultIngredientParser}
import org.specs2.matcher.DataTables
import org.specs2.mutable.Specification

/**
  * Created by U6017873 on 7/7/2014.
  */
/*
class MakeListDataTablesSpec extends Specification with DataTables with StrategyComponent{ def is =
   "tests for making a groceryList.list from ingredients"  ! e1

   val combiner = parser

   def e1 =
     "line1"   | "line2" | "line3" | "expectedListSize" |
       "2 cups butter"  !! "1 cup butter"  ! "3 c. butter" ! 1 |
       "1 tbsp. salt"  !! "1 tbsp. salt" ! "2 teaspoons salt" ! 2 |
       "1 teaspoon salt"  !! "1 cup salt" ! "3 tablespoons salt" ! 3 |> {
       (line1,line2,line3,expectedListSize) => {
         val lines = Seq(line1,line2,line3).map(combiner.fromLine(_)).flatten
         combiner.mergeIngredients(lines, Seq()) must haveSize(expectedListSize)
       }
     }
 }
*/
class MergeIngredientsSpec extends Specification with StrategyComponent{
  val listMaker = parser

  "Given two ingredients with the same name and same unit, merge" should {
    val first = Ingredient("butter", Some(1.0), Some(WellKnownUnitOfMeasure("cup")))
    val second = Ingredient("butter", Some(2.0), Some(WellKnownUnitOfMeasure("cup")))
    val merged = listMaker.mergeIngredients(first, second)

    "Combine them into one item" in {
      merged must haveSize(1)
    }

    "Use the name as the list item text" in {
      merged(0).text must equalTo("butter")
    }

    "Use the same unit as the list items" in {
      merged(0).unit must equalTo(Some(WellKnownUnitOfMeasure("cup")))
    }

    "Add the amounts" in {
      merged(0).amount must beSome(3.0)
    }

    "Include the source ingredients in the list item" in {
      merged(0).sourceIngredients must containAllOf(Seq(first, second))
    }
  }

  "Given two ingredients with the same name and different units, merge" should {

    val first = Ingredient("butter", Some(1.0), Some(WellKnownUnitOfMeasure("cup")))
    val second = Ingredient("butter", Some(2.0), Some(WellKnownUnitOfMeasure("tablespoons")))
    val merged = listMaker.mergeIngredients(first, second)

    "Not merge them" in {
      merged must haveSize(2)
    }
  }
}
