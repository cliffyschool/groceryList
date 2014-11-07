package groceryList.rest

import groceryList.actors.ParseIngredientActor.IngredientParsed
import groceryList.actors.{GatherIngredientsResponse, GatherIngredientsRequest}
import groceryList.model.{UnitOfMeasure, WellKnownUnitOfMeasure, UnknownUnitOfMeasure, Ingredient}
import spray.json.{JsString, JsValue, RootJsonFormat, DefaultJsonProtocol}
import spray.json._

object JsonProtocol extends DefaultJsonProtocol {
  implicit val gatherRequestFormat = jsonFormat1(GatherIngredientsRequest)
  implicit val unknownUnitFormat = jsonFormat1(UnknownUnitOfMeasure)
  implicit val knownUnitFormat = jsonFormat1(WellKnownUnitOfMeasure)
  implicit object UnitOfMeasureFormat extends RootJsonFormat[UnitOfMeasure] {
    def write(a: UnitOfMeasure) = a match {
      case k: WellKnownUnitOfMeasure => JsObject("name" -> JsString(k.name), "known" -> JsBoolean(true))
      case u: UnknownUnitOfMeasure => JsObject("name" -> JsString(u.name), "known" -> JsBoolean(false))
    }
    def read(value: JsValue) =
      value.asJsObject.fields("known") match {
        case JsBoolean(true) =>  value.convertTo[WellKnownUnitOfMeasure]
        case _ => value.convertTo[UnknownUnitOfMeasure]
      }
  }
  implicit val ingFormat = jsonFormat3(Ingredient)
  implicit val ingParsedFormat = jsonFormat1(IngredientParsed)
  implicit val gatherResponseFormat = jsonFormat1(GatherIngredientsResponse)
}
