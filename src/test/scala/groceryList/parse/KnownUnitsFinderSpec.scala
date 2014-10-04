package groceryList.parse

import groceryList.model.KnownUnitOfMeasure
import org.specs2.mutable.Specification

/**
 * Created by cfreeman on 10/3/14.
 */
class KnownUnitsFinderSpec extends Specification {

  val known = KnownUnitsFinder(Map("fathom" -> Seq("fathoms", "fath", "f", "f.")))
  val unitString = "fathom"

  "Given a known unit string, finder" should {

    val unit = known.get(unitString)

    "return something..." in {
      unit must beSome[KnownUnitOfMeasure]
    }

    "...that is marked as known" in {
      unit.get.known must beTrue
    }

    "...and has a name matching the unit string" in {
      unit.get.name must equalTo(unitString)
    }
  }

  "Given an abbreviation for a known unit, the finder" should {
    val unit = known.get("fath")

    "return something..." in {
      unit must beSome
    }

    "...that is marked as known" in {
      unit.get.known must beTrue
    }

    "...with the proper unit string as the name" in {
      unit.get.name must equalTo("fathom")
    }
  }

  "Given an abbreviation ending in ., the finder" should {
    val unit = known.get("f.")

    "return something..." in {
      unit must beSome
    }

    "...that is marked as known" in {
      unit.get.known must beTrue
    }

    "...with the proper unit string as the name" in {
      unit.get.name must equalTo("fathom")
    }
  }


  "Given an unknown unit string, the finder" should {
    val unit = known.get("hey")

    "return none" in {
      unit must beNone
    }
  }
}
