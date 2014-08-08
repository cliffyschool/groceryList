package groceryList.actors

import ParseIngredientActor.{IngredientParsed, NoIngredientParsed, ParseIngredient}
import akka.actor.{Actor,ActorSystem}
import groceryList.model.{UnitOfMeasure, Ingredient}
import groceryList.parse.{IngredientParser, DefaultIngredientParser}
import akka.channels._

/**
 * Created by cfreeman on 7/29/14.
 */
class ParseIngredientActor extends Actor
with Channels[TNil, (ParseIngredient,ParseResponse) :+: TNil]
{
  channel[ParseIngredient] {
    case (ParseIngredient(line), sender) =>
      val i = DefaultIngredientParser.parse(line) match {
        case None => NoIngredientParsed(line)
        case Some(i) => IngredientParsed(i)
      }
      sender <-!- i
  }

  /*
  def receive: Receive = {
    case ParseIngredient(line) =>
      val i = DefaultIngredientParser.parse(line) match {
        case None => NoIngredientParsed(line)
        case Some(i) => IngredientParsed(i)
      }
      sender ! i
  }
  */
}

trait ParseResponse

object ParseIngredientActor {
  case class ParseIngredient(line: String)
  case class IngredientParsed(i: Ingredient) extends ParseResponse
  case class NoIngredientParsed(from: String) extends ParseResponse
}
