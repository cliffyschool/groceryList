package domain

import domain.line.Line


case class ListItem(
text:String,
amount: Option[Double],
unit: Option[UnitOfMeasure],
sourceIngredients: Seq[Line]
)
