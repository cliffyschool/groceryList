package groceryList.rest

import groceryList.actors.ParseIngredientActor.IngredientParsed
import groceryList.actors.{GatherIngredientsResponse, GatherIngredientsRequest, Core, CoreActors}
import groceryList.model.WellKnownUnitOfMeasure
import org.specs2.mutable.Specification
import spray.http.StatusCodes._
import spray.httpx.SprayJsonSupport._
import spray.json.DefaultJsonProtocol
import spray.routing.Directives
import JsonProtocol._
import spray.testkit.Specs2RouteTest

class MakeListSpec extends Specification with Directives with Specs2RouteTest with Core with CoreActors {

  val path = "list"

//TODO: this spec reads badly

  "list endpoint should support" >> {
    "get list by id" in {
      val route = new ListService(gatherActor).listRoute
      Get(s"/$path/abc") ~> route ~> check {
        val gatherResponse = responseAs[GatherIngredientsResponse]
        gatherResponse.results must not beEmpty
      }
    }

    "post ingredients to list" in {
      val route = new ListService(gatherActor).listRoute
      Post(s"/$path", GatherIngredientsRequest("an ingredient")) ~> route ~> check {
        responseAs[String] must not beNull
      }
    }

    "post ingredients, then get list" in {
      val route = new ListService(gatherActor).listRoute
      val listId = Post(s"/$path", GatherIngredientsRequest("1 cup butter\n2 tbsp. sugar")) ~> route ~> check {responseAs[String]}
      Thread.sleep(500)
      val listContent = Get(s"/$path/$listId") ~> route ~> check {responseAs[GatherIngredientsResponse]}
      listContent.results must haveSize(2)
      listContent.results(0) must beAnInstanceOf[IngredientParsed]
    }
  }
}
