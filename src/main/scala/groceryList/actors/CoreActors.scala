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

  val parseSystem = ChannelExt(system).actorOf(new ParseIngredientActor() , "handler")

  implicit val timeout = Timeout(5, TimeUnit.SECONDS)

}
