package groceryList.parse

import groceryList.model.UnitOfMeasure

/**
 * Created by U6017873 on 7/14/2014.
 */
case class KnownUnits(unitsWithAbbreviations: Map[String, groceryList.model.UnitOfMeasure]) {
  def find(str: String): Option[groceryList.model.UnitOfMeasure] = unitsWithAbbreviations.get(str)
}


object KnownUnits {

  case class AbbrevList(abbrevs: Map[String,Seq[String]])
  implicit def al(list: Map[String,Seq[String]]) = AbbrevList(list)

  def apply(unitsWithAbbreviations: AbbrevList): KnownUnits = {
    val m =
      unitsWithAbbreviations.abbrevs
        .map(kv => (UnitOfMeasure(kv._1, true), kv._2.+:(kv._1)))
        .map(kv => kv._2.map(abbrev => (abbrev, kv._1)))
        .flatten
        .toMap
    KnownUnits(m)
  }
}
