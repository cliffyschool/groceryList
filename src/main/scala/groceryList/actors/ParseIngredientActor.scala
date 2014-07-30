package groceryList.actors

import ParseIngredientActor.{IngredientParsed, NoIngredientParsed, ParseIngredient}
import akka.actor.Actor
import groceryList.model.Ingredient
import groceryList.parse.{IngredientParser, DefaultIngredientParser}

/**
 * Created by cfreeman on 7/29/14.
 */
class ParseIngredientActor extends Actor {
  this:IngredientParser =>

  def receive: Receive = {
    case ParseIngredient(line) =>
      val i = DefaultIngredientParser.parse(line) match {
        case None => NoIngredientParsed(line)
        case Some(i) => IngredientParsed(i)
      }
      sender ! i
  }
}

object ParseIngredientActor {
  case class ParseIngredient(line: String)
  case class IngredientParsed(i: Ingredient)
  case class NoIngredientParsed(from: String)
}
