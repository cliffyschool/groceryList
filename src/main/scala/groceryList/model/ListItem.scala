package groceryList.model

case class ListItem(text:String, amount: Option[Double], unit: Option[UnitOfMeasure], sourceIngredients: Seq[Ingredient])
