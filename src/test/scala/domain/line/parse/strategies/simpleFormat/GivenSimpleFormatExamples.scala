package domain.line.parse.strategies.simpleFormat

import domain.UnknownUnitOfMeasure
import domain.line.{LineParserStrategyComponent, LineParserStrategy, Line}
import domain.line.parse.strategies.ParseStrategyTest
import org.specs2.matcher.DataTables
import org.specs2.mutable.Specification


class GivenSimpleFormatExamples extends Specification
with DataTables
with ParseStrategyTest
with LineParserStrategyComponent { override def is =

  "tests for parsing simple format"  ! examples

  override def parse = LineParserStrategy.simpleFormat

  def examples =
    "line" | "expectedAmount" | "expectedUnit" | "expectedName" |
    "4 dips butter" !! Some(4.0d) ! Some(UnknownUnitOfMeasure("dips")) ! "butter" |
    "3.75 dips of butter" !! Some(3.75d) ! Some(UnknownUnitOfMeasure("dips")) ! "butter" |> {

      (line, expectedAmount, expectedUnit, expectedName) => {
        val expected = Line(expectedName, expectedAmount, expectedUnit)
        parse(line) must beSome(expected)
      }
    }
}
