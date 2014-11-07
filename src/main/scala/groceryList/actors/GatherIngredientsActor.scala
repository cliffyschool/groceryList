package groceryList.actors

import akka.actor.{ActorRef, Props, Actor}
import groceryList.actors.ParseIngredientActor.{NoIngredientParsed, IngredientParsed, ParseIngredient}
import akka.util.Timeout
import akka.pattern.{ ask, pipe }
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Created by cfreeman on 8/10/14.
 */
class GatherIngredientsActor(parserRef: ActorRef) extends Actor {

  implicit val timeout = Timeout(5.seconds)

  def receive = {
      case GatherIngredientsRequest(fromText) ⇒
        val lines = fromText.split("\n").toList.par
        val msgs =
        lines.map { line =>
          (parserRef ? ParseIngredient(line)).map {
            case p: IngredientParsed => GatherIngredientsResponse(Option(p))
            case n: NoIngredientParsed => GatherIngredientsResponse(None)
          }
        }
        msgs.map(futureResponse => futureResponse pipeTo sender)
    }
}

case class GatherIngredientsRequest(fromText: String)
case class GatherIngredientsResponse(parsed: Option[IngredientParsed])
