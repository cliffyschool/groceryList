package domain.line.parse.strategies.unitWithNumber

import domain.line.LineParserStrategies
import domain.line.parse.strategies.ParseStrategyTest
import org.specs2.mutable.Specification

class NumericallyQualifiedUnitSpec extends Specification with ParseStrategyTest {

  override def parse = new LineParserStrategies().numericallyQualifiedUnit

  "Given a line with no known units, it" should {
    "return none" in {
      parse("3 4-ab. chicken breasts") must beNone
    }
  }

  "Given a line with only 1 known unit, it" should {
    "return none" in {
      parse("6 4 oz. pork chops") must beNone
    }
  }

}
