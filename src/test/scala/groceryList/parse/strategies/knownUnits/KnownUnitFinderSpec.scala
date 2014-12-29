package groceryList.parse.strategies.knownUnits

import groceryList.model.WellKnownUnitOfMeasure
import groceryList.parse.WellKnownUnitsFinder
import org.specs2.mutable.Specification

/**
 * Created by cfreeman on 10/3/14.
 */
class KnownUnitFinderSpec extends Specification {

  val known = WellKnownUnitsFinder(Map("fathom" -> Seq("fathoms", "fath", "f", "f.")))
  val unitString = "fathom"

  "Given a known unit name, matchKnownUnit" should {

    val unit = known.matchKnownUnit(unitString)

    "return a result" in {
      unit must beSome[WellKnownUnitOfMeasure]
    }

    "return a result with the right name" in {
      unit.get.name must equalTo(unitString)
    }
  }

  "Given an abbreviation for a known unit, matchKnownUnit" should {
    val unit = known.matchKnownUnit("fath")

    "return a result" in {
      unit must beSome
    }

    "return a result with the proper unit string as the name" in {
      unit.get.name must equalTo("fathom")
    }
  }

  "Given a unit abbreviation ending in ., matchKnownUnit" should {
    val unit = known.matchKnownUnit("f.")

    "return a result" in {
      unit must beSome
    }

    "return a result with the proper unit string as the name" in {
      unit.get.name must equalTo("fathom")
    }
  }


  "Given an unknown unit string, matchKnownUnit" should {
    val unit = known.matchKnownUnit("hey")

    "return none" in {
      unit must beNone
    }
  }
}
