package groceryList.actors

import akka.actor.{ActorSystem, Props}
import groceryList.parse.{IngredientParser, DefaultIngredientParser}
import akka.util.Timeout
import java.util.concurrent.TimeUnit

/**
 * Created by cfreeman on 7/29/14.
 */
trait CoreActors {
  this: Core =>


  val ourSystem = ActorSystem("system")
  def parseActor = ourSystem.actorOf(Props(new ParseIngredientActor))
  def gatherActor = ourSystem.actorOf(Props(new GatherIngredientsActor))

  implicit val timeout = Timeout(5, TimeUnit.SECONDS)

}


