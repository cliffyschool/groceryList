package application.actors

import akka.actor.{ActorSystem, Props}
import domain.line.LineParserComponent
import akka.util.Timeout
import java.util.concurrent.TimeUnit

trait CoreActors extends LineParserComponent {
  this: Core =>


  val ourSystem = ActorSystem("system")

  def parseActor = ourSystem.actorOf(Props(new LineActor(parser)))
  def listActor = ourSystem.actorOf(Props(new ListActor()))

  implicit val timeout = Timeout(5, TimeUnit.SECONDS)

}


