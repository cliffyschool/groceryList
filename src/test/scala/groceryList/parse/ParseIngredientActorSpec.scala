package groceryList.parse


import groceryList.actors.{ParseIngredientActor, CoreActors, Core}
import ParseIngredientActor.{NoIngredientParsed, IngredientParsed, ParseIngredient}
import akka.testkit.{ImplicitSender, TestKit}
import akka.actor.ActorSystem
import akka.channels._
import org.specs2.mutable.SpecificationLike
import scala.concurrent.duration.{FiniteDuration, Duration}
import akka.util.Timeout
import java.util.concurrent.TimeUnit

/**
 * Created by cfreeman on 7/29/14.
 */
class ParseIngredientActorSpec  extends TestKit(ActorSystem())
with SpecificationLike
with ImplicitSender
with CoreActors
with Core {

  "groceryList.parse actor" should {

    sequential
    implicit val selfChannel: ChannelRef[(Any, Nothing) :+: TNil] = new ChannelRef(testActor)

    "send a parsed ingredient message for a valid line" in {
        parseSystem <-!- ParseIngredient("1 cup butter")
        expectMsgType[IngredientParsed] must not beNull
    }

    "send a no-parsed-ingredient message for a blank line" in {
      parseSystem <-!- ParseIngredient("")
      expectMsgType[NoIngredientParsed] must not beNull
    }
  }
}
