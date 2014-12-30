package domain.line.parse.strategies.simpleFormat

import domain.line.LineParserStrategy
import domain.line.parse.strategies.ParseStrategyTest
import org.specs2.mutable.Specification

class SimpleFormatStrategySpec extends Specification with ParseStrategyTest{

  override def parse = LineParserStrategy.simpleFormat

  "Given a line with less than 3 chunks, it" should {
    "not return a result" in {
      parse("4 onions") must beNone
    }
  }

  "Given a line with more than 4 chunks, it" should {
    "not return a result" in {
      parse("4 really large hunks of meat") must beNone
    }
  }
}
