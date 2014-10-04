package groceryList.actors

import akka.actor.ActorSystem
import groceryList.model.Ingredient
import groceryList.parse.{ParseIngredientStrategy, DefaultIngredientParser}

/**
 * Created by cfreeman on 7/29/14.
 */
trait Core {

  protected implicit def system: ActorSystem


}
