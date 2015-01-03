package controllers

import java.util.UUID

import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout
import application.actors.LineActor.LineCreated
import application.actors.{CreateList, ListCreated}
import domain.WellKnownUnitOfMeasure
import domain.line.Line
import spray.http.StatusCodes
import spray.httpx.Json4sSupport
import spray.httpx.SprayJsonSupport._
import spray.routing.Directives

import scala.collection.parallel.mutable
import scala.collection.parallel.mutable.ParMap
import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.util.{Failure, Success}
import org.json4s.DefaultFormats

case class ListId(id: String)

class ListService(gatherActor: ActorRef)(implicit ec:ExecutionContext) extends Directives with Json4sSupport {

  implicit val timeout = Timeout(5 seconds)

  // TODO: build repo for lists
  val responses = ParMap("abc" -> domain.List(Seq(Line("butter", Some(1), Some(WellKnownUnitOfMeasure("cup"))))))

  val json4sFormats = DefaultFormats



  val listRoute =
    get {
      pathSingleSlash {
        redirect("/list", StatusCodes.Found)
      } ~
        path("list") {
          complete(responses.values.seq)
        } ~
        path("list" / Segment) { id =>
          val list = responses.get(id)
          complete(list)
        }
    } ~
      post {
        path("list") {
          handleWith { gatherRequest: CreateList =>
              val id = UUID.randomUUID().toString
              (gatherActor ? gatherRequest)
              .mapTo[ListCreated]
                .onComplete{
                case Success(r) =>
                  responses.put(id, r.results)
                case Failure(ex) => println("failure: " + ex); "failure"
              }
            ListId(id)
          }
        }
      }
}
