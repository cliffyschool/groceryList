package groceryList.actors

import ParseIngredientActor.{IngredientParsed, NoIngredientParsed, ParseIngredient}
import akka.actor.{ActorSystem, ActorLogging, Actor, Props}
import groceryList.model.{UnitOfMeasure, Ingredient}
import groceryList.parse.{IngredientParser, DefaultIngredientParser}

/**
 * Created by cfreeman on 7/29/14.
 */
class ParseIngredientActor(parser: IngredientParser) extends Actor
{
  def receive = {
    case (ParseIngredient(line, groupId)) =>
      val i = parser.fromLine(line) match {
        case None => NoIngredientParsed(line, groupId)
        case Some(ing) => IngredientParsed(ing, groupId)
      }
      println("sending to " + sender())
      sender() ! i
  }
}

trait ParseResponse

object ParseIngredientActor {
  case class ParseIngredient(line: String, groupId: String)
  case class IngredientParsed(ingredient: Ingredient, groupId: String) extends ParseResponse
  case class NoIngredientParsed(from: String, groupId: String) extends ParseResponse
}
