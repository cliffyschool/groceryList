package controllers

import application.actors.LineActor.{LineCreated, LineNotCreated}
import application.actors.{GatherIngredientsRequest, GatherIngredientsResponse, LineResponse}
import domain.line.Line
import domain.{UnitOfMeasure, UnknownUnitOfMeasure, WellKnownUnitOfMeasure}
import spray.json.{DefaultJsonProtocol, JsString, JsValue, RootJsonFormat, _}

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
  implicit val ingFormat = jsonFormat3(Line)
  implicit val noIngParsedFormat = jsonFormat2(LineNotCreated)
  implicit object ParseResponseFormat extends RootJsonFormat[LineResponse]{
    def write(a: LineResponse) = a match {
      case p: LineCreated => p.toJson
      case n: LineNotCreated => n.toJson
    }
    def read(value: JsValue) =
      value.asJsObject match {
        case o if o.fields("line") != null => value.convertTo[LineCreated]
        case _ => value.convertTo[LineNotCreated]
      }
  }
  implicit val ingParsedFormat = jsonFormat2(LineCreated)
  implicit val gatherResponseFormat = jsonFormat1(GatherIngredientsResponse)
}
