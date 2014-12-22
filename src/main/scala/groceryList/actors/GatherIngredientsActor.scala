package groceryList.actors

import akka.actor.{ActorRef, Props, Actor}
import akka.contrib.pattern.Aggregator
import groceryList.actors.ParseIngredientActor.{NoIngredientParsed, IngredientParsed, ParseIngredient}
import akka.util.Timeout
import akka.pattern.{ ask, pipe }
import groceryList.parse.{StrategyComponent, IngredientParser}
import scala.collection.mutable.ArrayBuffer
import scala.collection.parallel.mutable
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Created by cfreeman on 8/10/14.
 */
class GatherIngredientsActor(parserRef: ActorRef) extends Actor with Aggregator with StrategyComponent{

  import context._
  implicit val timeout = Timeout(5.seconds)

  expectOnce {
    case GatherIngredientsRequest(fromText) ⇒

      new IngredientAggregator(sender, parserRef, fromText)
    case _ ⇒
      //sender() ! CantUnderstand
      context.stop(self)
  }

  class IngredientAggregator(originalSender: ActorRef, parseActor: ActorRef, fromText: String)  {

    val responses = ArrayBuffer.empty[ParseResponse]

    val lines = fromText.split("\n").filter(s => s.trim.length > 0).toList.par
    lines.map { line =>
      parseActor ! ParseIngredient(line)
      expect {
        case p: IngredientParsed =>
          responses += p
          sendResponses
        case n: NoIngredientParsed =>
          responses += n
          sendResponses
      }
    }

    def sendResponses = {
      if (responses.size == lines.size){
        originalSender ! GatherIngredientsResponse(responses)
        context.stop(self)
      }
    }
  }
 }

case class GatherIngredientsRequest(fromText: String)
case class GatherIngredientsResponse(results: Seq[ParseResponse])
