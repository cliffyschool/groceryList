/**
 * Created by U6017873 on 7/14/2014.
 */
case class KnownUnits(unitsWithAbbreviations: Map[String, Unit]) {
  def find(str: String): Option[Unit] = unitsWithAbbreviations.get(str)
}

object KnownUnits {
  def apply(unitsWithAbbreviations: Seq[(String, Seq[String])]): KnownUnits = {
    val m =
      unitsWithAbbreviations
        .map(kv => (Unit(kv._1, true), kv._2.+:(kv._1)))
        .map(kv => kv._2.map(abbrev => (abbrev, kv._1)))
        .flatten
        .toMap
    KnownUnits(m)
  }
}
