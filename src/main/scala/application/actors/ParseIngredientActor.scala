package application.actors

import ParseIngredientActor.{IngredientParsed, NoIngredientParsed, ParseIngredient}
import akka.actor.{ActorSystem, ActorLogging, Actor, Props}
import domain.UnitOfMeasure
import domain.line.{LineParser, Line}

class ParseIngredientActor(parser: LineParser) extends Actor
{
  def receive = {
    case (ParseIngredient(line, groupId)) =>
      val i = parser.fromLine(line) match {
        case None => NoIngredientParsed(line, groupId)
        case Some(ing) => IngredientParsed(ing, groupId)
      }
      sender() ! i
  }
}

trait ParseResponse

object ParseIngredientActor {
  case class ParseIngredient(line: String, groupId: String)
  case class IngredientParsed(ingredient: Line, groupId: String) extends ParseResponse
  case class NoIngredientParsed(from: String, groupId: String) extends ParseResponse
}
