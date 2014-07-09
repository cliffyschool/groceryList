
/**
 * Created by U6017873 on 7/6/2014.
 *
 */
class Ingredients {


  val strategies = Array(ParseIngredientStrategy.assumeEasyFormat, ParseIngredientStrategy.assumeIngredientContainsNumbers, ParseIngredientStrategy.assumeNoUnits)

  def fromLine(line: String) : Option[Ingredient] = {

    for (strategy <- strategies) {
      val result = strategy(line match { case null => None case _ => Some(line)})
      if (result.isDefined)
        return result
    }
    return None

  }

  def makeList(l1: Seq[Ingredient], l2: Seq[Ingredient]) : Seq[Ingredient] = {
    val combined = (l1 ++ l2)
        .map{i => Ingredient(name=i.name,amount=i.amount,unit=i.unit)}
    val r = combined.groupBy{(a) => (a.name, a.unit)}.mapValues(_.reduce{(a,b) => Ingredient(a.name, a.amount+b.amount, a.unit)}).values.toList
    r
  }
}

case class Ingredient(name: String, amount: Double, unit: Option[Unit])

case class Unit(unit: String, known: Boolean)

object Ingredients {
  def main(args: Array[String]) {
    val line = readLine("Type an ingredient.")
    println(line)
  }
}
