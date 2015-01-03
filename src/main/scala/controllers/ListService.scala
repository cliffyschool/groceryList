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
import spray.httpx.SprayJsonSupport._
import spray.routing.Directives

import scala.collection.parallel.mutable
import scala.collection.parallel.mutable.ParMap
import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.util.{Failure, Success}

class ListService(gatherActor: ActorRef)(implicit ec:ExecutionContext) extends Directives {

  import JsonAdapters._
  implicit val timeout = Timeout(5 seconds)

  // TODO: build repo for lists
  val responses = ParMap("abc" -> ListCreated(domain.List(Seq(Line("butter", Some(1), Some(WellKnownUnitOfMeasure("cup")))))))

  val listRoute =
    get {
      pathSingleSlash {
        redirect("/list", StatusCodes.Found)
      } ~
        path("list") {
          complete(responses.values.seq)
        } ~
        path("list" / Segment) { id =>
          complete(responses.get(id))
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
                  responses.put(id, r)
                  id
                case Failure(ex) => println("failure: " + ex); "failure"
              }
            id
          }
        }
      }
}
