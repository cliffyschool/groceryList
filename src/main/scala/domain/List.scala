package domain

import domain.line.Line

case class List(lines: Seq[Line]) {

  def getListItems() : Seq[ListItem] = {
    lines.groupBy { (line) => (line.name, line.unit)}
      .map(entry => ListItem(
      entry._1._1,
      entry._2.map(ing => ing.amount).flatten.sum match { case 0 => None case d => Some(d)},
      entry._1._2,
      entry._2))
      .toSeq
  }

}
