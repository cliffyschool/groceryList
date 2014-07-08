import org.apache.commons.math.fraction.FractionFormat

/**
 * Created by U6017873 on 7/6/2014.
 */
class Ingredients {

  val amountRegex = "(a|[0-9/\\.]+)".r
  val synonyms = Map( "cups" -> "cups", "cup" -> "cups", "c." -> "cups",
                      "tablespoons"-> "tablespoons", "tbsp" -> "tablespoons", "tbsp." -> "tablespoons", "tablespoon" -> "tablespoons",
                      "teaspoon" -> "teaspoons", "teaspoons" -> "teaspoons", "tsp" -> "teaspoons", "tsp." -> "teaspoons",
                      "ounce" -> "ounces", "ounces" -> "ounces", "oz" -> "ounces", "oz." -> "ounces"
                      )
  val fractionFormat = new FractionFormat()

  def fromLine(line: String) : Option[Ingredient] = {
    line.split(" ") match {
      case Array(amountRegex(amount), unit, ingredient) =>   Some(Ingredient(ingredient, getAmount(amount), getUnit(unit)))
      case Array(amountRegex(amount), unit, "of", ingredient) => Some(Ingredient(ingredient, getAmount(amount), getUnit(unit)))
      case _ => None
    }
  }

  def makeList(l1: Seq[Ingredient], l2: Seq[Ingredient]) : Seq[Ingredient] = {
    val combined = (l1 ++ l2)
        .map{i => Ingredient(name=i.name,amount=i.amount,unit=getUnit(i.unit))}
    val r = combined.groupBy{(a) => (a.name, a.unit)}.mapValues(_.reduce{(a,b) => Ingredient(a.name, a.amount+b.amount, a.unit)}).values.toList
    r
  }



  def getAmount(amount : String) : Double = {
    amount match {
      case "a" => 1
      case s:String if s.contains("/") => fractionFormat.parse(amount).doubleValue()
      case s:String => s.toDouble
    }
  }

  def getUnit(unit : String) : String = {
    synonyms.get(unit) match {
      case Some(s) => s
      case None => unit
    }
  }

}

case class Ingredient(name: String, amount: Double, unit: String)

object Ingredients {
  def main(args: Array[String]) {
    val line = readLine("Type an ingredient.")
    println(line)
  }
}
