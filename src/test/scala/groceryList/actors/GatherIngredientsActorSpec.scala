package groceryList.actors

import akka.actor._
import akka.testkit._
import groceryList.actors.ParseIngredientActor._
import groceryList.model.Ingredient
import groceryList.parse.{StrategyComponent, DefaultIngredientParser}
import org.specs2.mutable._

class GatherIngredientsActorSpec extends Specification with StrategyComponent {

  class WithActors extends TestKit(ActorSystem("test")) with org.specs2.specification.Scope with ImplicitSender{
    val parseActor = TestProbe()
    val gatherActor = system.actorOf(Props(new GatherIngredientsActor(){
      override def parseActor = WithActors.this.parseActor.ref
    }))
  }

  "Given a request with 2 non-blank lines of text, it" should {
    "send 2 parse requests" in new WithActors {
      val twoValidIngredients = "1 cup butter\n1 tbsp. salt"
      gatherActor ! GatherIngredientsRequest(twoValidIngredients)

      Seq("1 cup butter", "1 tbsp. salt")
        .map { m =>
        val msg = parseActor.expectMsgType[ParseIngredient]
        msg.line must equalTo(m)
      }
    }
  }

  "Given a request with 1 non-blank line and 5 blank or whitespace lines, it" should {
    "send exactly 1 parse request" in new WithActors{
        val oneIngredient5BlankLines = "1 cup butter\n\n \n\n \n"
        gatherActor ! GatherIngredientsRequest(oneIngredient5BlankLines)
        val msg = parseActor.expectMsgType[ParseIngredient]
        msg.line must equalTo("1 cup butter")
        parseActor.expectNoMsg()
    }
  }

}

