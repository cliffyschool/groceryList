package domain.line

import domain.UnitOfMeasure

case class Line(
  name: String,
  amount: Option[Double],
  unit: Option[UnitOfMeasure]
)
