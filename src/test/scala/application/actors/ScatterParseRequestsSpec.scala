package application.actors

import akka.actor._
import akka.testkit._
import application.actors.ParseIngredientActor._
import domain.line.LineParserComponent
import org.specs2.matcher.ResultMatchers
import org.specs2.mutable._

class ScatterParseRequestsSpec extends Specification with LineParserComponent with ResultMatchers{

  class WithoutParseActor extends TestKit(ActorSystem("test")) with org.specs2.specification.Scope with ImplicitSender{
    val stubParseActor = TestProbe()
    val gatherActor = system.actorOf(Props(new GatherIngredientsActor(){
      override def parseActor = WithoutParseActor.this.stubParseActor.ref
    }))
  }
  
  "Given a request with 2 non-blank lines of text, it" should {
    "send 2 parse requests" in new WithoutParseActor {
      val twoValidIngredients = "1 cup butter\n1 tbsp. salt"
      gatherActor ! GatherIngredientsRequest(twoValidIngredients)

      val ingredientNames = (1 to 2).map(i => stubParseActor.expectMsgType[ParseIngredient].line)
      ingredientNames must contain("1 cup butter", "1 tbsp. salt")
    }
  }

  "Given a request with 1 non-blank line and 5 blank or whitespace lines, it" should {
    "send exactly 1 parse request" in new WithoutParseActor{
        val oneIngredient5BlankLines = "1 cup butter\n\n \n\n \n"
        gatherActor ! GatherIngredientsRequest(oneIngredient5BlankLines)
        val msg = stubParseActor.expectMsgType[ParseIngredient]
        msg.line must equalTo("1 cup butter")
        stubParseActor.expectNoMsg()
    }
  }

}

