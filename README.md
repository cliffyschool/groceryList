# README #

Scratch project for learning about Akka actors, plus a bit of Spray.

The idea is to take text from recipes' ingredients like

* 1/3 cup butter
* 1/3 cup chopped onion
* 2 tomatoes (diced)
* ...
* 1/2 cup butter
* ...

and parse it into a list of items with units of measure attached like

* Line("butter", Some(0.33), Some(WellKnownUnitOfMeasure("cup")))
* Line("tomatoes", Some(2.0), None)
* Line("chopped onion", Some(0.33), Some(WellKnownUnitOfMeasure("cup")))
* ...
* Line("butter", Some(0.5), Some(WellKnownUnitOfMeasure("cup")))

so that eventually a merged grocery list could be built from it like

* ListItem("butter", 0.83, Some(WellKnownUnitOfMeasure("cup")))
* ...
