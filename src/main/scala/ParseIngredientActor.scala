import ParseIngredientActor.{IngredientParsed, NoIngredientParsed, ParseIngredient}
import akka.actor.Actor

/**
 * Created by cfreeman on 7/29/14.
 */
class ParseIngredientActor extends Actor {
  def receive: Receive = {
    case ParseIngredient(line) =>
      val i = Ingredients.parse(line) match {
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
