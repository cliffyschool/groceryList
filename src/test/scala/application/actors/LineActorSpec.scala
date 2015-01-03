package application.actors

import akka.actor.{ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit}
import application.actors.LineActor.{LineCreated, LineNotCreated, CreateLine}
import domain.line.LineParserComponent
import org.specs2.mutable.Specification

class LineActorSpec extends Specification
with LineParserComponent{

  class WithLineActor extends TestKit(ActorSystem("test")) with org.specs2.specification.Scope with ImplicitSender {
    val lineActor = system.actorOf(Props(new LineActor(parser)))
  }

  "Given a line string, the line actor" should {
    "send back an LineCreated message" in new WithLineActor {
      lineActor ! CreateLine("1 cup butter", "123")
      val msg = expectMsgType[LineCreated]
      msg must not beNull
    }

    "send back a LineCreated message with details" in new WithLineActor {
      lineActor ! CreateLine("1 cup butter", "123")
      val msg = expectMsgType[LineCreated]
      msg.line must not beNull
    }
  }

  "Given a blank line, the line actor" should {
    "send a LineNotCreated message" in new WithLineActor {
      lineActor ! CreateLine("", "123")
      val msg = expectMsgType[LineNotCreated]
      msg must not beNull
    }
  }

}
