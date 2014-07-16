import org.specs2.matcher.Expectable
import org.specs2.mutable.Specification

/**
 * Created by U6017873 on 7/13/2014.
 */
class KnownUnitsSpec extends Specification {

  val knownUnits = Seq(
    "cup", "tablespoon", "pinch", "pound", "teaspoon", "can", "stick", "ounce", "jar"
  )

  def beAKnownUnit = beSome[Unit].which(u => u.known)

  "known units strategy" should {
    "find all known units in a string" in {
      val aLine = "cup cups hello hi tablespoons hey teaspoons ounce blah blee"
      ParseIngredientStrategy.detectKnownUnits(aLine) must haveSize(5)
    }

    "match all known units" in {
      knownUnits.map { uStr =>
        val u = ParseIngredientStrategy.matchKnownUnit(uStr)
        u must beAKnownUnit
      }
    }

    "use the first number as quantity" in {
      val u = ParseIngredientStrategy.assumeKnownUnit(Some("1 cup butter"))
      u must beSome(Ingredient(amount=Some(1),unit=Some(Unit("cup", true)), name="butter"))
    }

    "handle ratios as quantity" in {
      val u = ParseIngredientStrategy.assumeKnownUnit(Some("1 1/2 cups butter"))
      u must beSome(Ingredient(amount=Some(1.5),unit=Some(Unit("cup", true)), name="butter"))
    }

    "handle decimal values as quantity" in {
      val u = ParseIngredientStrategy.assumeKnownUnit(Some("1.53 cups butter"))
      u must beSome(Ingredient(amount=Some(1.53),unit=Some(Unit("cup", true)), name="butter"))
    }

    "ignore numbers after the first number and before the first unit" in {
      val u = ParseIngredientStrategy.assumeKnownUnit(Some("9 2 8 19 4.5 6 1/2 cup butter"))
      u must beSome(Ingredient(amount=Some(9),unit=Some(Unit("cup", true)), name="butter"))
    }

    "ignore everything between the first number and the first unit" in {
      val u = ParseIngredientStrategy.assumeKnownUnit(Some("4 tra lee, tra la, 2 8 19 4.5 6 1/2 cup butter"))
      u must beSome(Ingredient(amount=Some(4),unit=Some(Unit("cup", true)), name="butter"))
     }

    "use the first matched unit as the unit" in {
      val u = ParseIngredientStrategy.assumeKnownUnit(Some("1 cup tablespoon butter"))
      u must beSome(Ingredient(amount=Some(1),unit=Some(Unit("cup", true)), name="tablespoon butter"))
    }

    "return None if no known unit is detected" in {
      val u = ParseIngredientStrategy.assumeKnownUnit(Some("1 cupz butter"))
      u must beNone
    }

    "handle empty ingredient name" in {
      val u = ParseIngredientStrategy.assumeKnownUnit(Some("3 cups"))
      u must beSome(Ingredient(name="", amount = Some(3), unit = Some(Unit("cup", true))))
    }
  }

}
