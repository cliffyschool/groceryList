package groceryList.parse


import groceryList.actors._
import ParseIngredientActor.{NoIngredientParsed, IngredientParsed, ParseIngredient}
import akka.testkit.{ImplicitSender, TestKit}
import akka.actor.ActorSystem
import akka.channels._
import org.specs2.mutable.SpecificationLike
import scala.concurrent.duration.{FiniteDuration, Duration}
import akka.util.Timeout
import java.util.concurrent.TimeUnit
import groceryList.actors.ParseIngredientActor.NoIngredientParsed
import groceryList.actors.ParseIngredientActor.IngredientParsed
import groceryList.actors.GatherIngredientsRequest
import groceryList.actors.ParseIngredientActor.ParseIngredient

/**
 * Created by cfreeman on 7/29/14.
 */
class ParseIngredientActorSpec  extends TestKit(ActorSystem())
with SpecificationLike
with ImplicitSender
with CoreActors
with Core {

  implicit val selfChannel: ChannelRef[(Any, Nothing) :+: TNil] = new ChannelRef(testActor)


  "groceryList.parse actor" should {

    sequential
    "send a parsed ingredient message for a valid line" in {
        parseSystem <-!- ParseIngredient("1 cup butter")
        expectMsgType[IngredientParsed] must not beNull
    }

    "send a no-parsed-ingredient message for a blank line" in {
      parseSystem <-!- ParseIngredient("")
      expectMsgType[NoIngredientParsed] must not beNull
    }
  }
  "gather actor" should {
    sequential

    "work" in {
      gatherSystem <-!- GatherIngredientsRequest("1 cups butter")
      expectMsgType[GatherIngredientsResponse].msg must equalTo("cup")
    }
  }
}
