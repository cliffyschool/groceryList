package domain.line

import domain.ListItem
import commons.StringUtils._

trait LineParser {
  this:LineParserStrategyComponent =>

  def fromLine: (String) => Option[Line] = {
    case line if line.isNullOrEmpty => None
    case line =>
      strategies.toStream.flatMap (s => s._2(line)).headOption
  }

  def mergeIngredients(ingredients: Line*) : Seq[ListItem] = {
    ingredients.groupBy { (ingredient) => (ingredient.name, ingredient.unit)}
      .map(entry => ListItem(
      entry._1._1,
      entry._2.map(ing => ing.amount).flatten.sum match { case 0 => None case d => Some(d)},
      entry._1._2,
      entry._2))
      .toSeq
  }
}

trait LineParserComponent {
  val parser = new LineParser with LineParserStrategyComponent
}
