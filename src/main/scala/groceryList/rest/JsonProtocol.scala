package groceryList.rest

import groceryList.actors.ParseIngredientActor.{NoIngredientParsed, IngredientParsed}
import groceryList.actors.{ParseResponse, GatherIngredientsResponse, GatherIngredientsRequest}
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
  implicit val noIngParsedFormat = jsonFormat2(NoIngredientParsed)
  implicit object ParseResponseFormat extends RootJsonFormat[ParseResponse]{
    def write(a: ParseResponse) = a match {
      case p: IngredientParsed => p.toJson
      case n: NoIngredientParsed => n.toJson
    }
    def read(value: JsValue) =
      value.asJsObject match {
        case o if o.fields("ingredient") != null => value.convertTo[IngredientParsed]
        case _ => value.convertTo[NoIngredientParsed]
      }
  }
  implicit val ingParsedFormat = jsonFormat2(IngredientParsed)
  implicit val gatherResponseFormat = jsonFormat1(GatherIngredientsResponse)
}
