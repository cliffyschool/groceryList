package groceryList.listify

import groceryList.parse.DefaultIngredientParser
import org.specs2.Specification
import org.specs2.matcher.DataTables

/**
  * Created by U6017873 on 7/7/2014.
  */
class MakeListSpec extends Specification with DataTables{ def is =
   "tests for making a groceryList.list from ingredients"  ! e1

   val combiner = new DefaultIngredientParser

   def e1 =
     "line1"   | "line2" | "line3" | "expectedListSize" |
       "2 cups butter"  !! "1 cup butter"  ! "3 c. butter" ! 1 |
       "1 tbsp. salt"  !! "1 tbsp. salt" ! "2 teaspoons salt" ! 2 |
       "1 teaspoon salt"  !! "1 cup salt" ! "3 tablespoons salt" ! 3 |> {
       (line1,line2,line3,expectedListSize) => {
         val lines = Seq(line1,line2,line3).map(combiner.fromLine(_)).flatten
         combiner.makeList(lines, Seq()) must haveSize(expectedListSize)
       }
     }
 }
