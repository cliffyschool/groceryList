package application.actors

import akka.actor.{Props, ActorSystem}
import akka.testkit.{TestProbe, ImplicitSender, TestKit}
import application.actors.LineActor.CreateLine
import domain.line.LineParserComponent
import org.specs2.mutable.Specification

class ListActorSpec extends Specification with LineParserComponent{

  class WithListActor extends TestKit(ActorSystem("test")) with org.specs2.specification.Scope with ImplicitSender {
    val lineActor = system.actorOf(Props(new LineActor(parser)))
    val listActor = system.actorOf(Props(new ListActor()))
  }

  class WithStubLineActor extends TestKit(ActorSystem("test")) with org.specs2.specification.Scope with ImplicitSender{
    val stubParseActor = TestProbe()
    val listActor = system.actorOf(Props(new ListActor(){
      override def lineActor = WithStubLineActor.this.stubParseActor.ref
    }))
  }

  "Given a CreateList message, the list actor" should {
    "return a ListCreated message" in new WithListActor{
      listActor ! CreateList("1 cup sugar")

      expectMsgType[ListCreated]
    }
  }

  "Given a CreateList message with multiple lines, the list actor" should {
    "return a ListCreated message with two lines" in new WithListActor {
      listActor ! CreateList("1 cup sugar\n1 tbsp. pepper")

      val msg = expectMsgType[ListCreated]
      msg.results.lines must haveSize(2)
    }
  }

  "Given two CreateList messages, the list actor" should {
    "return two ListCreated messages" in new WithListActor {
      listActor ! CreateList("1 cup wheat")
      listActor ! CreateList("1 cup rice")
      expectMsgType[ListCreated]
      expectMsgType[ListCreated]
    }
  }

  "Given a CreateList message with 2 non-blank lines of text, the list actor" should {
    "send 2 CreateLine messages" in new WithStubLineActor {
      val twoValidLines = "1 cup butter\n1 tbsp. salt"
      listActor ! CreateList(twoValidLines)

      val lineNames = (1 to 2).map(i => stubParseActor.expectMsgType[CreateLine].lineString)
      lineNames must contain("1 cup butter", "1 tbsp. salt")
    }
  }

  "Given a CreateList request with 1 non-blank line and 5 blank or whitespace lines, it" should {
    "send exactly 1 CreateLine message" in new WithStubLineActor{
      val oneIngredient5BlankLines = "1 cup butter\n\n \n\n \n"
      listActor ! CreateList(oneIngredient5BlankLines)
      val msg = stubParseActor.expectMsgType[CreateLine]
      msg.lineString must equalTo("1 cup butter")
      stubParseActor.expectNoMsg()
    }
  }
}
