package controllers

import java.util.UUID

import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout
import application.actors.{CreateList, ListCreated}
import domain.ListRepository
import org.json4s.DefaultFormats
import spray.http.StatusCodes
import spray.httpx.Json4sSupport
import spray.routing.Directives

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.util.{Failure, Success}

case class ListId(id: String)

class ListService(listActor: ActorRef, listRepository: ListRepository)(implicit ec:ExecutionContext) extends Directives
with Json4sSupport {

  implicit val timeout = Timeout(5 seconds)
  implicit val json4sFormats = DefaultFormats + new UnitOfMeasureSerializer

  val listRoute =
    get {
      pathSingleSlash {
        redirect("/list", StatusCodes.Found)
      } ~
        path("list") {
          val lists = listRepository.getAll
          complete(lists)
        } ~
        path("list" / Segment) { id =>
          val list = listRepository.findById(id)
          complete(list)
        }
    } ~
      post {
        path("list") {
          handleWith {
            gatherRequest: CreateList =>
              val id = UUID.randomUUID().toString
              (listActor ? gatherRequest)
              .mapTo[ListCreated]
                .onComplete{
                case Success(r) =>
                  listRepository.save(id, r.results)
                case Failure(ex) => println("failure: " + ex); "failure"
              }
            ListId(id)
          }
        }
      }
}
