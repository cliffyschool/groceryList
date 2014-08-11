package groceryList.actors

import akka.actor.Actor
import akka.channels._
import groceryList.actors.ParseIngredientActor.{NoIngredientParsed, IngredientParsed, ParseIngredient}
import akka.util.Timeout
import scala.concurrent.duration._

/**
 * Created by cfreeman on 8/10/14.
 */
class GatherIngredientsActor(target: => Actor
  with Channels[TNil, (ParseIngredient, ParseResponse) :+: TNil])
  extends Actor
  with Channels[TNil, (GatherIngredientsRequest, GatherIngredientsResponse) :+: TNil] {

  implicit val timeout = Timeout(5.seconds)
  import context.dispatcher

  lazy val targetRef = createChild(target, "parseSystem")

  channel[GatherIngredientsRequest] {
    case (request, sender) ⇒
      ParseIngredient("hello") -?-> targetRef -*-> (_.map{
        case p:IngredientParsed => GatherIngredientsResponse(p.i.name)
        case n:NoIngredientParsed => GatherIngredientsResponse("nope")
      }) -!-> sender
  }
}

case class GatherIngredientsRequest(fromText: String)
case class GatherIngredientsResponse(msg:String)
