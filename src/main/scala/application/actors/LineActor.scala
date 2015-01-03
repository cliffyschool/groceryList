package application.actors

import akka.actor.Actor
import application.actors.LineActor.{CreateLine, LineCreated, LineNotCreated}
import domain.line.{Line, LineParser}

class LineActor(parser: LineParser) extends Actor
{
  def receive = {
    case (CreateLine(line, groupId)) =>
      val i = parser.fromLine(line) match {
        case None => LineNotCreated(line, groupId)
        case Some(ing) => LineCreated(ing, groupId)
      }
      sender() ! i
  }
}

trait LineResponse

object LineActor {
  case class CreateLine(lineString: String, groupId: String)
  case class LineCreated(line: Line, groupId: String) extends LineResponse
  case class LineNotCreated(from: String, groupId: String) extends LineResponse
}
