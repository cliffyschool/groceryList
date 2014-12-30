package domain.line

import domain.{WellKnownUnitOfMeasure, UnitOfMeasure}

case class WellKnownUnitsFinder(unitsWithAbbreviations: Map[String, UnitOfMeasure]) {
  def matchKnownUnit(name: String) =
    unitsWithAbbreviations.get(name).flatMap(u => Some(WellKnownUnitOfMeasure(u.name)))
}

object WellKnownUnitsFinder {

  case class AbbrevList(abbrevs: Map[String,Seq[String]])
  implicit def al(list: Map[String,Seq[String]]) = AbbrevList(list)

  def apply(unitsWithAbbreviations: AbbrevList): WellKnownUnitsFinder = {
    val m =
      unitsWithAbbreviations.abbrevs
        .map(kv => (WellKnownUnitOfMeasure(kv._1), kv._2.+:(kv._1)))
        .map(kv => kv._2.map(abbrev => (abbrev, kv._1)))
        .flatten
        .toMap
    WellKnownUnitsFinder(m)
  }
}
