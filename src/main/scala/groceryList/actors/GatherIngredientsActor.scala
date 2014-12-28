package groceryList.actors

import java.util.UUID

import akka.actor.{Actor, ActorRef, Props}
import groceryList.actors.ParseIngredientActor.{IngredientParsed, NoIngredientParsed, ParseIngredient}
import groceryList.parse.StrategyComponent

import scala.collection.mutable

class GatherIngredientsActor extends Actor with StrategyComponent {
  import collection.mutable.{ HashMap, MultiMap, Set }
  val responses = new mutable.HashMap[String, mutable.Set[ParseResponse]]
    with mutable.MultiMap[String, ParseResponse]

  val expectedResponseCount = collection.mutable.HashMap[String, (Int,ActorRef)]()

  def parseActor = context.actorOf(Props(new ParseIngredientActor(parser)))

  override def receive = {
    case GatherIngredientsRequest(fromText) =>
      val requestId = UUID.randomUUID().toString
      val lines = fromText.split("\n").filter(s => s.trim.length > 0).toList.par
      expectedResponseCount += requestId -> (lines.size, sender())
      lines.map(s => ParseIngredient(s, requestId)).map(m => parseActor ! m)
    case p: IngredientParsed =>
      responses.addBinding(p.groupId, p)
      sendResponses(p.groupId)
    case n: NoIngredientParsed =>
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
case class GatherIngredientsResponse(results: Seq[ParseResponse])
