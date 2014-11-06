package groceryList.rest

import groceryList.actors.ParseIngredientActor.IngredientParsed
import groceryList.actors.{GatherIngredientsResponse, GatherIngredientsRequest}
import groceryList.model.{WellKnownUnitOfMeasure, UnknownUnitOfMeasure, Ingredient}
import spray.json.DefaultJsonProtocol

object JsonProtocol extends DefaultJsonProtocol {
  implicit val orderFormat = jsonFormat1(GatherIngredientsRequest)
  implicit val orderConfirmationFormat = jsonFormat1(GatherIngredientsResponse)
}
