package groceryList.rest

import java.util.UUID

import akka.actor.ActorRef
import akka.util.Timeout
import groceryList.actors.{GatherIngredientsRequest, GatherIngredientsResponse}
import spray.httpx.SprayJsonSupport._
import spray.routing.Directives
import akka.pattern.ask
import spray.routing.PathMatchers.Segment
import scala.concurrent.duration._

import scala.concurrent.ExecutionContext

class GatherIngredientsService(gather: ActorRef)(implicit ec:ExecutionContext) extends Directives {

  import JsonProtocol._

  var responses = Map[String,GatherIngredientsResponse]()

  val gatherRoute =
  path("list" / Segment) { id: String =>
    get {
        complete(responses.getOrElse(id, "none").toString)
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
