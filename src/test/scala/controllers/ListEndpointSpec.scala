package controllers

import application.actors.LineActor.LineCreated
import application.actors.{Core, CoreActors, CreateList, ListCreated}
import controllers.JsonAdapters._
import domain.line.Line
import org.specs2.mutable.Specification
import spray.httpx.SprayJsonSupport._
import spray.routing.Directives
import spray.testkit.Specs2RouteTest

class ListEndpointSpec extends Specification with Directives with Specs2RouteTest with Core with CoreActors {

  val path = "list"

//TODO: this spec reads badly

  "/list" >> {
    "GET /list/abc" in {
      val route = new ListService(gatherActor).listRoute
      Get(s"/$path/abc") ~> route ~> check {
        val gatherResponse = responseAs[ListCreated]
        gatherResponse.results.lines must not beEmpty
      }
    }

    "POST GatherIngredientsRequest" in {
      val route = new ListService(gatherActor).listRoute
      Post(s"/$path", CreateList("an ingredient")) ~> route ~> check {
        responseAs[String] must not beNull
      }
    }

    "POST, then GET" in {
      val route = new ListService(gatherActor).listRoute
      val listId = Post(s"/$path", CreateList("1 cup butter\n2 tbsp. sugar")) ~> route ~> check {responseAs[String]}
      Thread.sleep(500)
      val listContent = Get(s"/$path/$listId") ~> route ~> check {responseAs[ListCreated]}
      listContent.results.lines must haveSize(2)
    }
  }
}
