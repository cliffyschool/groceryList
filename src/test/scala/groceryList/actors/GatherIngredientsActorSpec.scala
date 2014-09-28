package groceryList.actors

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import org.specs2.mutable.SpecificationLike

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
}
