package groceryList.actors

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import groceryList.actors.ParseIngredientActor.{IngredientParsed, NoIngredientParsed, ParseIngredient}
import org.specs2.mutable.SpecificationLike
import akka.pattern.{ ask, pipe }

/**
 * Created by cfreeman on 7/29/14.
 */
class ParseIngredientActorSpec  extends TestKit(ActorSystem())
with SpecificationLike
with ImplicitSender
with CoreActors
with Core {


  "Given a parseable line, the parse actor" should {

    parseActor ! ParseIngredient("1 cup butter")
    val msg = expectMsgType[IngredientParsed]

    "send back an IngredientParsed message" in {
      msg must not beNull
    }

    "...with ingredient details" in {
      msg.i must not beNull
    }
  }

  "Given a blank line, the parse actor" should {
    parseActor ! ParseIngredient("")

    val msg = expectMsgType[NoIngredientParsed]

    "send a no-ingredient-parsed message" in {
      msg must not beNull
    }

  }

}
