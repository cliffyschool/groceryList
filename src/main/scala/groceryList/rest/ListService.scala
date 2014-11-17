package groceryList.rest

import java.util.UUID

import akka.actor.ActorRef
import akka.util.Timeout
import groceryList.actors.ParseIngredientActor.IngredientParsed
import groceryList.actors.{GatherIngredientsRequest, GatherIngredientsResponse}
import groceryList.model.{WellKnownUnitOfMeasure, Ingredient}
import spray.http.{StatusCodes, MediaTypes}
import spray.httpx.SprayJsonSupport._
import spray.json
import spray.routing.Directives
import akka.pattern.ask
import spray.routing.PathMatchers.Segment
import scala.concurrent.duration._

import scala.concurrent.ExecutionContext

class ListService(gatherActor: ActorRef)(implicit ec:ExecutionContext) extends Directives {

  import spray.json._
  import JsonProtocol._
  implicit val timeout = Timeout(5 seconds)

  var responses = Map[String, GatherIngredientsResponse]("abc" -> GatherIngredientsResponse(Seq(IngredientParsed(Ingredient("butter", Some(1), Some(WellKnownUnitOfMeasure("cup")))))))

  val listRoute =
    get {
      pathSingleSlash {
        redirect("/list", StatusCodes.Found)
      } ~
        path("list") {
          complete(responses)
        } ~
        path("list" / Segment) { id =>
          complete(responses.get(id))
        }
    } ~
      post {
        path("list") {
          handleWith { gatherRequest: GatherIngredientsRequest =>
              val id = UUID.randomUUID().toString
              val futResponse = gatherActor ? gatherRequest
              futResponse.onSuccess {
                case r: GatherIngredientsResponse =>
                  responses = responses + (id -> r)
              }
              futResponse.onFailure{case e => println("fails!"+e)}
            id
          }
        }
      }
}
