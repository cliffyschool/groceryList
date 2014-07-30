package groceryList.actors

import akka.actor.Props

/**
 * Created by cfreeman on 7/29/14.
 */
trait CoreActors {
  this: Core =>

  val parseSystem = system.actorOf(Props[ParseIngredientActor])

}
