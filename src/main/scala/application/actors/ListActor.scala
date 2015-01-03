package application.actors

import java.util.UUID

import akka.actor.{Actor, ActorRef, Props}
import application.actors.LineActor.{CreateLine, LineCreated, LineNotCreated}
import domain.line.LineParserComponent

import scala.collection.mutable

class ListActor extends Actor with LineParserComponent{
  import scala.collection.mutable.Set

  // TODO: stuff needs to go. An actor should not be managing which messages get mapped to
  // which responses.
  val responses = new mutable.HashMap[String, mutable.Set[LineResponse]]
    with mutable.MultiMap[String, LineResponse]
  val expectedResponseCount = collection.mutable.HashMap[String, (Int,ActorRef)]()

  def lineActor = context.actorOf(Props(new LineActor(parser)))

  override def receive = {
    case CreateList(fromText) =>
      val requestId = UUID.randomUUID().toString
      val lines = fromText.split("\n").filter(s => s.trim.length > 0).toList.par
      expectedResponseCount += requestId -> (lines.size, sender())
      lines.map(s => CreateLine(s, requestId)).map(m => lineActor ! m)
    case p: LineCreated =>
      responses.addBinding(p.groupId, p)
      sendResponses(p.groupId)
    case n: LineNotCreated =>
      responses.addBinding(n.groupId, n)
      sendResponses(n.groupId)

  }

  def sendResponses(requestId: String) = {
    val resp = responses.getOrElse(requestId, Set()).toSeq
    val expected = expectedResponseCount.getOrElse(requestId, (0, sender()))
    if (resp.size == expected._1) {
      val lines = resp.map{ case l:LineCreated => l.line}
      val list = domain.List(lines)
      expected._2 ! ListCreated(list)
    }
  }
}

// TODO: add message trait
case class CreateList(fromText: String)
case class ListCreated(results: domain.List)
