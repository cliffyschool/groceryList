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

    "use first number as quantity" in {
      val u = Seq(Some("1 cup butter")).map(ParseIngredientStrategy.assumeKnownUnit)
      u.flatten.seq(0).amount must equalTo(1)
    }
  }

}
