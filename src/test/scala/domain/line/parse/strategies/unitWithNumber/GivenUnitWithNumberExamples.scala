package domain.line.parse.strategies.unitWithNumber

import domain.UnknownUnitOfMeasure
import domain.line.parse.strategies.ParseStrategyTest
import domain.line.{Line, LineParserStrategies, LineParserStrategiesComponent}
import org.specs2.matcher.DataTables
import org.specs2.mutable.Specification


class GivenUnitWithNumberExamples extends Specification
 with DataTables
 with ParseStrategyTest
 with LineParserStrategiesComponent { override def is =

   "tests for parsing lines wherein the unit is qualified by a numeric (e.g., 'one 10 1/2 can of tomatoes')"  ! examples

   override def parse = new LineParserStrategies().numericallyQualifiedUnit

   def examples =
     "line" | "expectedAmount" | "expectedUnit" | "expectedName" |
     "3 10 1/2 oz cans diced tomatoes" !! Some(3.0d) ! Some(UnknownUnitOfMeasure("10.5 ounce can")) ! "diced tomatoes" |
  //  TODO: test 40-oz jug "of" beer
     "1 40-oz. jug beer" !! Some(1d) ! Some(UnknownUnitOfMeasure("40.0 ounce jug")) ! "beer" |> {

       (line, expectedAmount, expectedUnit, expectedName) => {
         val expected = Line(expectedName, expectedAmount, expectedUnit)
         parse(line) must beSome(expected)
       }
     }
 }
