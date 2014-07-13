import scala.collection.mutable

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
    val r = combined.groupBy { (ingredient) => (ingredient.name, ingredient.unit)}
      .mapValues(_.reduce { (left, right) => Ingredient(left.name, combineAmounts(left, right), left.unit)})
      .values.toList
    r
  }

  def combineAmounts(left: Ingredient, right: Ingredient): Option[Double] = {
    (left.amount, right.amount) match {
      case (Some(l), Some(r)) => Some(l + r)
      case (Some(l), None) => Some(l)
      case (None, Some(r)) => Some(r)
    }
  }
}

case class Ingredient(name: String, amount: Option[Double], unit: Option[Unit])

case class Unit(unit: String, known: Boolean)

object Ingredients {
  def main(args: Array[String]) {
    println("Type an ingredient, or ok to build list")
    var list = mutable.MutableList[Option[Ingredient]]()
    val i = new Ingredients
    do {
      val line = readLine()
      if ("ok" == line) {
        println(list.size)
        val l = i.makeList(list.toSeq.flatten, Seq())
        l.foreach(println)
        return
      }
      else
        list += i.fromLine(line)
    } while (true)
  }
}
