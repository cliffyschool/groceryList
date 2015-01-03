package domain.line.parse

import domain.line.{LineParserComponent, Line}
import domain.{WellKnownUnitOfMeasure, UnknownUnitOfMeasure}
import application.actors.Core
import org.specs2.matcher.DataTables
import org.specs2.Specification

class ParseAmountsSpec extends Specification with DataTables with LineParserComponent { def is =
  "tests for parsing ingredients"  ! e1

  def e1 =
    "line" | "expectedAmount" | "expectedUnit" | "unitIsKnown" | "expectedIngredient" |
      "2 cups butter" !! Some(2.0d) ! Some(WellKnownUnitOfMeasure("cup")) ! true ! "butter" |
      "2 cups of butter" !! Some(2.0d) ! Some(WellKnownUnitOfMeasure("cup")) ! true ! "butter" |
      "a cup of butter" !! Some(1.0d) ! Some(WellKnownUnitOfMeasure("cup")) ! true ! "butter" |
      "1 cup butter" !! Some(1.0d) ! Some(WellKnownUnitOfMeasure("cup")) ! true ! "butter" |
      "1/2 c. butter" !! Some(0.5d) ! Some(WellKnownUnitOfMeasure("cup")) ! true ! "butter" |
      ".375 c. butter" !! Some(0.375d) ! Some(WellKnownUnitOfMeasure("cup")) ! true ! "butter" |
      "4 boneless pork chops" !! Some(4d) ! None ! false ! "boneless pork chops" |
      "6 4 oz. boneless pork chops" !! Some(6d) ! None ! false ! "4 oz. boneless pork chops" |
      "a 4 oz. boneless pork chop" !! Some(1d) ! None ! false ! "4 oz. boneless pork chop" |
      "6 (4 oz) boneless pork chops" !! Some(6d) ! None ! false ! "(4 oz) boneless pork chops" |
      "rice" !! None ! None ! false ! "rice" |
      "jasmine rice" !! None ! None ! false ! "jasmine rice" |
      "1 chicken" !! Some(1d) ! None ! false ! "chicken" |
      "2 10.5 oz cans diced tomatoes" !! Some(2d) ! Some(UnknownUnitOfMeasure("10.5 ounce can")) ! false ! "diced tomatoes" |
      "2 10.5 oz. cans diced tomatoes" !! Some(2d) ! Some(UnknownUnitOfMeasure("10.5 ounce can")) ! false ! "diced tomatoes" |
      "2 10.5-oz. cans diced tomatoes" !! Some(2d) ! Some(UnknownUnitOfMeasure("10.5 ounce can")) ! false ! "diced tomatoes" |
      "2 10.5-oz cans diced tomatoes" !! Some(2d) ! Some(UnknownUnitOfMeasure("10.5 ounce can")) ! false ! "diced tomatoes" |
      "2 10 1/2 oz cans diced tomatoes" !! Some(2d) ! Some(UnknownUnitOfMeasure("10.5 ounce can")) ! false ! "diced tomatoes" |
      "0.375 cup butter" !! Some(0.375d) ! Some(WellKnownUnitOfMeasure("cup")) ! true ! "butter" |> {
      (line, expectedAmount, expectedUnit, expectedUnitIsKnown, name) => {
        val expected = Line(name,expectedAmount,expectedUnit)
        parser.fromLine(line) must beSome(expected)
      }
    }
}
