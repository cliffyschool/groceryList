package groceryList.actors

import akka.actor.{ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit}
import groceryList.actors.ParseIngredientActor.{IngredientParsed, NoIngredientParsed, ParseIngredient}
import groceryList.parse.StrategyComponent
import org.specs2.mutable.Specification

class ParseIngredientsSpec extends Specification
with StrategyComponent {

  class WithParseActor extends TestKit(ActorSystem("test")) with org.specs2.specification.Scope with ImplicitSender {
    val parseActor = system.actorOf(Props(new ParseIngredientActor(parser)))
  }

  "Given a parseable line, the parse actor" should {
    "send back an IngredientParsed message" in new WithParseActor {
      parseActor ! ParseIngredient("1 cup butter", "123")
      val msg = expectMsgType[IngredientParsed]
      msg must not beNull
    }

    "send back an IngredientParsed message with ingredient details" in new WithParseActor {
      parseActor ! ParseIngredient("1 cup butter", "123")
      val msg = expectMsgType[IngredientParsed]
      msg.ingredient must not beNull
    }
  }

  "Given a blank line, the parse actor" should {
    "send a no-ingredient-parsed message" in new WithParseActor {
      parseActor ! ParseIngredient("", "123" +
        "")
      val msg = expectMsgType[NoIngredientParsed]
      msg must not beNull
    }
  }

}
