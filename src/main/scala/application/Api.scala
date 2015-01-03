package application

import application.actors.{Core, CoreActors}
import controllers.ListService
import spray.routing.HttpService

import scala.concurrent.ExecutionContext
import scala.concurrent.ExecutionContext.Implicits.global

trait Api extends HttpService with CoreActors with Core{
  val routes =
      new ListService(listActor, null).listRoute
}
