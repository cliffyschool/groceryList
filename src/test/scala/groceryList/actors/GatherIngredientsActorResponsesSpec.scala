package groceryList.actors

import akka.actor.{Props, ActorSystem}
import akka.testkit.{TestProbe, ImplicitSender, TestKit}
import groceryList.parse.StrategyComponent
import org.specs2.mutable.Specification

/**
 * Created by cfreeman on 12/27/14.
 */
class GatherIngredientsActorResponsesSpec extends Specification with StrategyComponent{

  class WithActors extends TestKit(ActorSystem("test")) with org.specs2.specification.Scope with ImplicitSender {
    val parseActor = system.actorOf(Props(new ParseIngredientActor(parser)))
    val gatherActor = system.actorOf(Props(new GatherIngredientsActor()))
  }


  "Given a request, the gather actor" should {
    "return a response" in new WithActors{
      gatherActor ! GatherIngredientsRequest("1 cup sugar")

      expectMsgType[GatherIngredientsResponse]
    }
  }

  "Given a request with multiple lines, the gather actor" should {
    "return a response with two results" in new WithActors {
      gatherActor ! GatherIngredientsRequest("1 cup sugar\n1 tbsp. pepper")

      val msg = expectMsgType[GatherIngredientsResponse]
      msg.results must haveSize(2)
    }
  }

  "Given two requests, the gather actor" should {
    "return two responses" in new WithActors {
      gatherActor ! GatherIngredientsRequest("1 cup wheat")
      gatherActor ! GatherIngredientsRequest("1 cup rice")
      expectMsgType[GatherIngredientsResponse]
      expectMsgType[GatherIngredientsResponse]
    }
  }
}
