package groceryList.actors

import akka.actor.Props
import akka.channels.ChannelExt
import groceryList.parse.{IngredientParser, DefaultIngredientParser}
import akka.util.Timeout
import java.util.concurrent.TimeUnit

/**
 * Created by cfreeman on 7/29/14.
 */
trait CoreActors {
  this: Core =>

  val parseActor = new ParseIngredientActor()
  val parseSystem = ChannelExt(system).actorOf(parseActor, "handler")
  val gatherSystem = ChannelExt(system).actorOf(new GatherIngredientsActor(parseActor), "gather")

  implicit val timeout = Timeout(5, TimeUnit.SECONDS)

}


