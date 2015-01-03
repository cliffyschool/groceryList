package application.actors

import java.util.UUID

import akka.actor.{Actor, ActorRef, Props}
import application.actors.LineActor.{LineCreated, LineNotCreated, CreateLine}
import domain.line.LineParserComponent

import scala.collection.mutable

class GatherIngredientsActor extends Actor with LineParserComponent{
  import collection.mutable.{ HashMap, MultiMap, Set }
  val responses = new mutable.HashMap[String, mutable.Set[LineResponse]]
    with mutable.MultiMap[String, LineResponse]

  val expectedResponseCount = collection.mutable.HashMap[String, (Int,ActorRef)]()

  def parseActor = context.actorOf(Props(new LineActor(parser)))

  override def receive = {
    case GatherIngredientsRequest(fromText) =>
      val requestId = UUID.randomUUID().toString
      val lines = fromText.split("\n").filter(s => s.trim.length > 0).toList.par
      expectedResponseCount += requestId -> (lines.size, sender())
      lines.map(s => CreateLine(s, requestId)).map(m => parseActor ! m)
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
    if (resp.size == expected._1)
      expected._2 ! GatherIngredientsResponse(resp)
  }
}

case class GatherIngredientsRequest(fromText: String)
case class GatherIngredientsResponse(results: Seq[LineResponse])
