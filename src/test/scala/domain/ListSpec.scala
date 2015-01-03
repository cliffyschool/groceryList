package domain

import domain.line.{Line, LineParserComponent}
import org.specs2.mutable.Specification

class ListSpec extends Specification with LineParserComponent{

  val cup = Some(WellKnownUnitOfMeasure("cup"))

  "When two lines have the same name and unit, listItems" should {
    val list = domain.List(Seq(
      Line("butter", Some(1.0), cup),
      Line("butter", Some(2.0), cup)))
    val listItems = list.getListItems()

    "merge them into one ListItem" in {
      listItems must haveSize(1)
    }

    "Use the name as the ListItem text" in {
      listItems(0).text must equalTo("butter")
    }

    "Use the same unit as the list items" in {
      listItems(0).unit must equalTo(cup)
    }

    "Add the amounts" in {
      listItems(0).amount must beSome(3.0)
    }

    "Include the source ingredients in the list item" in {
      listItems(0).sourceIngredients must containAllOf(list.lines)
    }
  }

  "When two lines have the same name, but different units, listItems" should {

    val first = Line("butter", Some(1.0), Some(WellKnownUnitOfMeasure("cup")))
    val second = Line("butter", Some(2.0), Some(WellKnownUnitOfMeasure("tablespoons")))
    val list = domain.List(Seq(first, second))
    val merged = list.getListItems()

    "Not merge them" in {
      merged must haveSize(2)
    }
  }
}
