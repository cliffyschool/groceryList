package application

import application.actors.{BootedCore, Core, CoreActors}

object Rest extends App with BootedCore with Core with CoreActors with Api {

}
