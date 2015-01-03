package controllers

import application.actors.{Core, CoreActors, CreateList}
import domain.line.Line
import domain.{ListRepository, UnknownUnitOfMeasure}
import org.json4s.DefaultFormats
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import spray.http.StatusCodes._
import spray.httpx.Json4sSupport
import spray.routing.Directives
import spray.testkit.Specs2RouteTest

class ListServiceSpec extends Specification with Directives with Specs2RouteTest
with Core
with CoreActors
with Json4sSupport
with Mockito{


  val path = "list"

  val json4sFormats = DefaultFormats + new UnitOfMeasureSerializer

  //TODO: this spec reads badly

  "GET /list/nonExistantId" should {
    val listRepo = mock[ListRepository]
    listRepo.findById(anyString).returns(None)
    val route = new ListService(listActor, listRepo).listRoute

    "return NotFound for a nonExistant id" in {
      Get("/list/nonExistantId") ~> route ~> check {
        status must equalTo(spray.http.StatusCodes.NotFound)
      }
    }
  }

  "GET /list/idOfExistingList" should {
    val listRepo = mock[ListRepository]
    val listId = "123"
    val list = domain.List(Seq(Line("test", Some(1.0), Some(UnknownUnitOfMeasure("myUnit")))))
    listRepo.findById(anyString).returns(Some(list))
    val route = new ListService(listActor, listRepo).listRoute

    "return the list" in {
      Get(s"/list/$listId") ~> route ~> check {
        val t = responseAs[domain.List]
        t must equalTo(list)
      }
    }
  }

  "POST a valid CreateList request" should {
    val route = new ListService(listActor, mock[ListRepository]).listRoute
    // TODO: reusing ListActor messages for HTTP requests. Need to split out.
    val request = CreateList("some butter")

    "return an id" in {
      Post(s"/$path", request) ~> route ~> check {
        responseAs[ListId] must not beNull
      }
    }
  }

  "POST junk" should {
    val route = new ListService(listActor, mock[ListRepository]).listRoute
    val request = "{hey}"

    "return a BadRequest" in {
      Post("/list", request) ~> route ~> check {
        println("\n\nstatus:" + status)
        status must equalTo(BadRequest)
      }
    }.pendingUntilFixed
  }
}
