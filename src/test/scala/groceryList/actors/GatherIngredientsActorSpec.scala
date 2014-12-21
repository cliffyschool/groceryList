package groceryList.actors

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import groceryList.actors._
import groceryList.actors.ParseIngredientActor._
import groceryList.parse.StrategyComponent
import org.specs2.mutable.SpecificationLike
import akka.pattern.{ ask, pipe }
import scala.concurrent.duration.FiniteDuration

/**
 * Created by cfreeman on 9/28/14.
 */
class GatherIngredientsActorSpec  extends TestKit(ActorSystem())
with SpecificationLike
with ImplicitSender
with CoreActors
with Core
with StrategyComponent
{
  sequential

  "Given a single valid ingredient line, the gatherIngredients actor" should {
    gatherActor ! GatherIngredientsRequest("1 cups butter")
    val msg = expectMsgType[GatherIngredientsResponse]

    "get back a response" in {
      msg must not beNull
    }
  }

  "Given several valid ingredient lines, the gatherIngredients actor" should {
    gatherActor ! GatherIngredientsRequest("1 cups butter\n2 tbsp. sugar")

    "send a message for each line" in {
      val msg = expectMsgClass(classOf[GatherIngredientsResponse])
      msg.results must haveSize(2)
    }
  }

  "Given 2 valid ingredient lines and one invalid ingredient line, the actor" should {
    gatherActor ! GatherIngredientsRequest("1 cup butter\n2 cups sugar\n1 tbsp. salt")
    val msg = expectMsgClass(classOf[GatherIngredientsResponse])
    
    "send a response with 3 results" in {
      msg.results must haveSize(3)
    }

    "send back 1 ingredient-not-parsed result" in {
      msg.results must contain((r: ParseResponse) => r.getClass must equalTo(classOf[NoIngredientParsed]))
    }
  }

}
