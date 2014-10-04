package groceryList.parse

import groceryList.model.{KnownUnitOfMeasure, UnitOfMeasure}

/**
 * Created by U6017873 on 7/14/2014.
 */
case class KnownUnitsFinder(unitsWithAbbreviations: Map[String, groceryList.model.UnitOfMeasure]) {
  def get(name: String) =
    unitsWithAbbreviations.get(name).flatMap(u => Some(KnownUnitOfMeasure(u.name)))
}

object KnownUnitsFinder {

  case class AbbrevList(abbrevs: Map[String,Seq[String]])
  implicit def al(list: Map[String,Seq[String]]) = AbbrevList(list)

  def apply(unitsWithAbbreviations: AbbrevList): KnownUnitsFinder = {
    val m =
      unitsWithAbbreviations.abbrevs
        .map(kv => (KnownUnitOfMeasure(kv._1), kv._2.+:(kv._1)))
        .map(kv => kv._2.map(abbrev => (abbrev, kv._1)))
        .flatten
        .toMap
    KnownUnitsFinder(m)
  }
}
