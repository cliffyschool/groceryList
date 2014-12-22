package groceryList.actors

import akka.actor._
import akka.testkit._
import groceryList.actors.ParseIngredientActor._
import org.specs2.mutable._

class GatherIngredientsActorSpec extends Specification {

  class WithActors extends TestKit(ActorSystem("test")) with org.specs2.specification.Scope {
    val parseActor = TestProbe()
    val gatherActor = system.actorOf(Props(new GatherIngredientsActor(parseActor.ref)))
  }

  "Given a request with 2 non-blank lines of text, it" should {
    "send 2 parse requests" in new WithActors {
        val twoValidIngredients = "1 cup butter\n1 tbsp. salt"
        gatherActor ! GatherIngredientsRequest(twoValidIngredients)

        val expectedMessages = Seq(ParseIngredient("1 cup butter"),ParseIngredient("1 tbsp. salt"))
        parseActor.expectMsgAllOf(expectedMessages :_*)
     }
  }

  "Given a request with 1 non-blank line and 5 blank or whitespace lines, it" should {
    "send exactly 1 parse request" in new WithActors{
        val oneIngredient5BlankLines = "1 cup butter\n\n \n\n \n"
        gatherActor ! GatherIngredientsRequest(oneIngredient5BlankLines)
        parseActor.expectMsg(ParseIngredient("1 cup butter"))
        parseActor.expectNoMsg()
    }
  }
}
