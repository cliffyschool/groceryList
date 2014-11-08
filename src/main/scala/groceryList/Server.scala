package groceryList

import groceryList.actors.{BootedCore, Core, CoreActors}
import groceryList.rest.Api

object Rest extends App with BootedCore with Core with CoreActors with Api {

}
