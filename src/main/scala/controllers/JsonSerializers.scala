package controllers

import domain.{UnitOfMeasure, UnknownUnitOfMeasure, WellKnownUnitOfMeasure}
import org.json4s.CustomSerializer
import org.json4s.JsonAST.{JBool, JField, JObject, JString}

class UnitOfMeasureSerializer extends CustomSerializer[UnitOfMeasure](format => ( {
  case JObject(List(JField("name", JString(name)), JField("known", JBool(true)))) => WellKnownUnitOfMeasure(name)
  case JObject(List(JField("name", JString(name)), JField("known", JBool(false)))) => UnknownUnitOfMeasure(name)
  },
  {
  case WellKnownUnitOfMeasure(name) => JObject(JField("name", JString(name)), JField("known", JBool(true)))
  case UnknownUnitOfMeasure(name) => JObject(JField("name", JString(name)), JField("known", JBool(false)))
}))
