package domain.line



trait LineParserStrategyComponent {

  val strategies = Array[Strategy](
    "easy" -> LineParserStrategy.simpleFormat,
    "ingredientWithNumbers" -> LineParserStrategy.numericallyQualifiedUnit,
    "knownUnit" -> LineParserStrategy.knownUnit,
    "noUnits" -> LineParserStrategy.noUnits,
    "itemNameOnly" -> LineParserStrategy.itemNameOnly
  )
}
