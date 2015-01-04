package application

import application.actors.{Core, CoreActors}
import controllers.ListService
import domain.{StubListRepository}
import spray.routing.HttpService

import scala.concurrent.ExecutionContext.Implicits.global

trait Api extends HttpService with CoreActors with Core {
  val listRepository = new StubListRepository
  val listService =
      new ListService(listActor, listRepository)
  val routes = listService.listRoute
}
