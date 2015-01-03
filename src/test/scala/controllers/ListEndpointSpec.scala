package controllers

import application.actors.LineActor.LineCreated
import application.actors.{Core, CoreActors, CreateList, ListCreated}
import domain.line.Line
import org.json4s.DefaultFormats
import org.specs2.mutable.Specification
import spray.httpx.Json4sSupport
import spray.httpx.SprayJsonSupport._
import spray.routing.Directives
import spray.testkit.Specs2RouteTest

class ListEndpointSpec extends Specification with Directives with Specs2RouteTest with Core with CoreActors with Json4sSupport {


  val path = "list"

  val json4sFormats = DefaultFormats

//TODO: this spec reads badly

  "/list" >> {
    "GET /list/abc" in {
      val route = new ListService(listActor).listRoute
      Get(s"/$path/abc") ~> route ~> check {
        val list = responseAs[domain.List]
        list.lines must not beEmpty
      }
    }

    "POST GatherIngredientsRequest" in {
      val route = new ListService(listActor).listRoute
      Post(s"/$path", CreateList("some butter")) ~> route ~> check {
        responseAs[ListId] must not beNull
      }
    }

    "POST, then GET" in {
      val route = new ListService(listActor).listRoute
      val listId = Post(s"/$path", CreateList("1 cup butter\n2 tbsp. sugar")) ~> route ~> check {responseAs[ListId]}
      Thread.sleep(500)
      val list = Get(s"/$path/${listId.id}") ~> route ~> check {responseAs[domain.List]}
      list.lines must haveSize(2)
    }
  }
}
