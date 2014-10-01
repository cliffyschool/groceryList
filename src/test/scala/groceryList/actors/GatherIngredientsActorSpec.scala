package groceryList.actors

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
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
with Core {

  "Given a single valid ingredient line, the gatherIngredients actor" should {
    gatherActor ! GatherIngredientsRequest("1 cups butter")
    val msg = expectMsgType[GatherIngredientsResponse]

    "get back a response" in {
      msg must not beNull
    }
  }

  "Given several valid ingredient lines, the gatherIngredients actor" should {
    gatherActor ? GatherIngredientsRequest("1 cups butter\n2 tbsp. sugar")

    "send a message for each line" in {
      val msgs = expectMsgAllClassOf(classOf[GatherIngredientsResponse])
      msgs.size must equalTo(2)
    }
  }
}
