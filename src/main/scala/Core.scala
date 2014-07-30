import akka.actor.ActorSystem

/**
 * Created by cfreeman on 7/29/14.
 */
trait Core {

  protected implicit def system: ActorSystem

}
