package application.actors

import akka.actor.{Props, ActorRefFactory, ActorSystem}
import akka.io.IO
import application.{Api, RoutedHttpService}
import spray.can.Http

trait BootedCore extends Core with Api {

  def system: ActorSystem = ActorSystem("groceryList")
  def actorRefFactory: ActorRefFactory = system
  val rootService = system.actorOf(Props(new RoutedHttpService(routes /*~ staticResources*/ )))

  IO(Http)(system) ! Http.Bind(rootService, "0.0.0.0", port = 8080)

  /**
   * Construct the ActorSystem we will use in our application
   */
  //protected implicit  val system : ActorSystem

  /**
   * Ensure that the constructed ActorSystem is shut down when the JVM shuts down
   */
  sys.addShutdownHook(system.shutdown())
}
