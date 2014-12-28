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
import scala.collection.parallel.mutable
import scala.concurrent.duration._
import mutable.ParMap
import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

class ListService(gatherActor: ActorRef)(implicit ec:ExecutionContext) extends Directives {

  import spray.json._
  import JsonProtocol._
  implicit val timeout = Timeout(5 seconds)

  val responses = ParMap("abc" -> GatherIngredientsResponse(Seq(IngredientParsed(Ingredient("butter", Some(1), Some(WellKnownUnitOfMeasure("cup"))), "123"))))
  val listRoute =
    get {
      pathSingleSlash {
        redirect("/list", StatusCodes.Found)
      } ~
        path("list") {
          complete(responses.values.seq)
        } ~
        path("list" / Segment) { id =>
          println("Getting " + id)
          responses.keys.map(k => "Have " + k).map(println)
          complete(responses.get(id))
        }
    } ~
      post {
        path("list") {
          handleWith { gatherRequest: GatherIngredientsRequest =>
              val id = UUID.randomUUID().toString
              println("Generated " + id)
              val result =
              (gatherActor ? gatherRequest)
              .mapTo[GatherIngredientsResponse]
                .onComplete{
                case Success(r) =>
                  println("succeeded for id " + id)
                  responses.put(id, r);
                  println("responses now has " + responses.size);
                  id
                case Failure(ex) => println("failure: " + ex); "failure"
              }
            id
          }
        }
      }
}
