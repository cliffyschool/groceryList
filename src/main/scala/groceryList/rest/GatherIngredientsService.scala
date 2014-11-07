package groceryList.rest

import java.util.UUID

import akka.actor.ActorRef
import akka.util.Timeout
import groceryList.actors.ParseIngredientActor.IngredientParsed
import groceryList.actors.{GatherIngredientsRequest, GatherIngredientsResponse}
import groceryList.model.{WellKnownUnitOfMeasure, Ingredient}
import spray.httpx.SprayJsonSupport._
import spray.routing.Directives
import akka.pattern.ask
import spray.routing.PathMatchers.Segment
import scala.concurrent.duration._

import scala.concurrent.ExecutionContext

class GatherIngredientsService(gather: ActorRef)(implicit ec:ExecutionContext) extends Directives {

  import JsonProtocol._

  var responses = Map[String,GatherIngredientsResponse]("abc" -> GatherIngredientsResponse(Some(IngredientParsed(Ingredient("butter", Some(1), Some(WellKnownUnitOfMeasure("cup")))))))

  val gatherRoute =
  path("list" / Segment) { id: String =>
    get {
        complete(responses.get(id))
    }
  } ~
  path("list") {
    post {
      handleWith {
          gatherRequest: GatherIngredientsRequest => {
            val id = UUID.randomUUID().toString
            implicit val timeout = Timeout(5 seconds)

            val futResponse = gather ? gatherRequest
            futResponse.map {
              case r: GatherIngredientsResponse =>
                responses = responses + (id -> r)
            }
            id
          }
      }
    }
  }
}
