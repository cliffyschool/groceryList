package application.actors

import java.util.concurrent.TimeUnit

import akka.actor.{ActorSystem, Props}
import akka.util.Timeout
import domain.line.LineParserComponent

trait CoreActors extends LineParserComponent {
  this: Core =>


  val ourSystem = ActorSystem("system")

  def parseActor = ourSystem.actorOf(Props(new LineActor(parser)))
  def listActor = ourSystem.actorOf(Props(new ListActor()))

  implicit val timeout = Timeout(5, TimeUnit.SECONDS)

}


