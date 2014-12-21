package groceryList.actors

import org.specs2.mutable._
import org.specs2.time.NoTimeConversions

import akka.actor._
import akka.testkit._
import scala.concurrent.duration._
import groceryList.parse.StrategyComponent
import groceryList.actors.ParseIngredientActor._

abstract class GatherRequest(gatherActor: ActorRef, msg: String) extends TestKit(ActorSystem()) 
with After
with ImplicitSender {
  gatherActor ! GatherIngredientsRequest(msg)
  val gatherResponse = expectMsgType[GatherIngredientsResponse]
  def after = system.shutdown()
}

class GatherIngredientsActorSpec2 extends Specification with Core with CoreActors with NoTimeConversions { 
  sequential 
  
val twoValidIngredients = "1 cup butter\n1 tbsp. salt"

  "Given a request with 2 ingredients, it" should {
      
      "get a response" in new GatherRequest(gatherActor,twoValidIngredients) {
        gatherResponse must not(beNull)
      }

      "get a response in 1 second" in new GatherRequest(gatherActor,twoValidIngredients) {
        within(1 second) {
          gatherResponse must not(beNull)
        }
       }

      "get a response with two results" in new GatherRequest(gatherActor, twoValidIngredients) {
        within(1 second) {
          gatherResponse.results must haveSize(2)
        }
      }
  }

  "Given a request with an unparseable ingredient, it" should {
    "get a NoIngredientParsed response" in new GatherRequest(gatherActor,"1 abc. bleh"){
      within(1 second) {
        gatherResponse.results(0) must beAnInstanceOf[NoIngredientParsed]
      }
    }
  }

  override def system = ActorSystem("test")
}
