package controllers

import application.actors.LineActor.{LineCreated, LineNotCreated}
import application.actors.{CreateList, ListCreated, LineResponse}
import domain.line.Line
import domain.{ListItem, UnitOfMeasure, UnknownUnitOfMeasure, WellKnownUnitOfMeasure}
import spray.json.{DefaultJsonProtocol, JsString, JsValue, RootJsonFormat, _}

object JsonAdapters extends DefaultJsonProtocol {

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
  implicit val liFormat = jsonFormat4(ListItem)
  implicit val listFormat: JsonFormat[domain.List] = jsonFormat1(domain.List)
  implicit val gatherResponseFormat = jsonFormat1(ListCreated)
  implicit val gatherRequestFormat = jsonFormat1(CreateList)
}
