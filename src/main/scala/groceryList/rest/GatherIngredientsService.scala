package groceryList.rest

import java.util.UUID

import akka.actor.ActorRef
import akka.util.Timeout
import groceryList.actors.ParseIngredientActor.IngredientParsed
import groceryList.actors.{GatherIngredientsRequest, GatherIngredientsResponse}
import groceryList.model.{WellKnownUnitOfMeasure, Ingredient}
import spray.http.MediaTypes
import spray.httpx.SprayJsonSupport._
import spray.json
import spray.routing.Directives
import akka.pattern.ask
import spray.routing.PathMatchers.Segment
import scala.concurrent.duration._

import scala.concurrent.ExecutionContext

class GatherIngredientsService(gatherActor: ActorRef)(implicit ec:ExecutionContext) extends Directives {

  import spray.json._
  import JsonProtocol._

  var responses = Map[String,GatherIngredientsResponse]("abc" -> GatherIngredientsResponse(Seq(IngredientParsed(Ingredient("butter", Some(1), Some(WellKnownUnitOfMeasure("cup")))))))

  val gatherRoute =
  path("list" / Segment) { id: String =>
    get {
        respondWithMediaType(MediaTypes.`application/json`)
      println(responses.size)
        complete(responses.get(id))
    }
  } ~
  path("list") {
    post {
      respondWithMediaType(MediaTypes.`application/json`)
      handleWith {
          val id = UUID.randomUUID().toString
          gatherRequest: GatherIngredientsRequest => {
            val id = UUID.randomUUID().toString
            implicit val timeout = Timeout(5 seconds)

            val futResponse = gatherActor ? gatherRequest
            futResponse.onSuccess {
              case r: GatherIngredientsResponse =>
                responses = responses + (id -> r)
            }
          }
          respondWithMediaType(MediaTypes.`application/json`)
          id
      }
    }
  }
}
