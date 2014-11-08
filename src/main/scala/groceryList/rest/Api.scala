package groceryList.rest

import groceryList.actors.{CoreActors, Core}
import spray.routing.HttpService
import scala.concurrent.ExecutionContext
import ExecutionContext.Implicits.global

trait Api extends HttpService with CoreActors with Core{
  val routes =
      new GatherIngredientsService(gatherActor).gatherRoute
}
