import org.specs2.mutable.Specification

/**
 * Created by U6017873 on 7/13/2014.
 */
class KnownUnitsSpec extends Specification {

  val knownUnits = Seq(
    "cup", "tablespoon", "pinch", "pound", "teaspoon", "can", "stick", "ounce", "fluid ounce"
  )


  "known units strategy" should {
    "find all known units in a string" in {
      val aLine = "cup cups hello hi tablespoons hey teaspoons ounce blah blee"
      ParseIngredientStrategy.detectKnownUnits(aLine) must haveSize(5)
    }

    "account for all known units" in {
      knownUnits.map { u =>
        val mtch = ParseIngredientStrategy.matchKnownUnit(u)
        mtch must beSome
        mtch.get.known must beTrue
      }
    }
  }

}
