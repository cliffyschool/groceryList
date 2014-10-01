package groceryList.actors

import akka.actor.{Props, Actor}
import groceryList.actors.ParseIngredientActor.{NoIngredientParsed, IngredientParsed, ParseIngredient}
import akka.util.Timeout
import akka.pattern.{ ask, pipe }
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Created by cfreeman on 8/10/14.
 */
class GatherIngredientsActor extends Actor {

  implicit val timeout = Timeout(5.seconds)

  val parserRef = context.actorOf(Props[ParseIngredientActor]) // will be destroyed and re-created upon restart by default

  def receive = {
      case GatherIngredientsRequest(fromText) ⇒
        val lines = fromText.split("\n")
        val msgs =
        lines.map { line =>
          (parserRef ? ParseIngredient(line)).map {
            case p: IngredientParsed => GatherIngredientsResponse(p.i.unit.get.name)
            case n: NoIngredientParsed => GatherIngredientsResponse("nope")
          }
        }
        msgs.map(m => m pipeTo sender)
    }
}

case class GatherIngredientsRequest(fromText: String)
case class GatherIngredientsResponse(msg:String)
