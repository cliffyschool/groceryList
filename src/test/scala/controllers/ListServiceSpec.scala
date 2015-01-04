package controllers

import application.actors.{Core, CoreActors, CreateList}
import domain.line.Line
import domain.{StubListRepository, ListRepository, UnknownUnitOfMeasure}
import org.json4s.DefaultFormats
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import spray.http.StatusCodes._
import spray.httpx.Json4sSupport
import spray.routing.Directives
import spray.testkit.Specs2RouteTest

class ListServiceSpec extends Specification
with Directives
with Specs2RouteTest
with Core
with CoreActors
with Json4sSupport
with Mockito{

  val json4sFormats = DefaultFormats + new UnitOfMeasureSerializer

  "GET /list" should {
    val listRepo = mock[ListRepository]
    val lists = Seq(domain.List(Seq(Line("test", Some(3.5), Some(UnknownUnitOfMeasure("myUnit"))))))
    listRepo.getAll.returns(lists)
    val route = new ListService(listActor, listRepo).listRoute

    "return all the lists" in {
      Get("/list") ~> route ~> check {
        responseAs[Seq[domain.List]] must equalTo(lists)
      }
    }
  }

  "GET /list/nonExistantId" should {
    val listRepo = new StubListRepository
    val listService = new ListService(listActor, listRepo)
    val route = listService.listRoute

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
    val listRepo = mock[ListRepository]
    val route = new ListService(listActor, listRepo).listRoute
    // TODO: reusing ListActor messages for HTTP requests. Need to split out.
    val request = CreateList("some butter")

    "return an id" in {
      Post(s"/list", request) ~> route ~> check {
        responseAs[ListId] must not beNull
      }
    }
  }

  "POST junk" should {
    val listRepo = mock[ListRepository]
    val route = new ListService(listActor, listRepo).listRoute
    val request = "hey" 
    
    "get rejected" in {
      Post("/list", request) ~> route ~> check {
        rejections must haveSize(1) 
        rejections(0) must beAnInstanceOf[spray.routing.MalformedRequestContentRejection]
      }
    }
  }
}
