import org.apache.commons.math.fraction.FractionFormat

/**
 * Created by U6017873 on 7/6/2014.
 *
 */
class Ingredients {

  val strategies = Seq(new AssumeEasyFormat, new AssumeIngredientContainsNumbers)

  def fromLine(line: String) : Option[Ingredient] = {

    val result = strategies.map(_.parseIngredient(Some(line))).flatten.headOption
    result match {
      case Some(r) => Some(r)
      case _ => None
      //case _ =>  case Array(amountRegex(amount), rest @ _*) => Some(Ingredient(rest.mkString(" "), getAmount(amount), None))
    }
  }

  def makeList(l1: Seq[Ingredient], l2: Seq[Ingredient]) : Seq[Ingredient] = {
    val combined = (l1 ++ l2)
        .map{i => Ingredient(name=i.name,amount=i.amount,unit=i.unit)}
    val r = combined.groupBy{(a) => (a.name, a.unit)}.mapValues(_.reduce{(a,b) => Ingredient(a.name, a.amount+b.amount, a.unit)}).values.toList
    r
  }
}

case class Ingredient(name: String, amount: Double, unit: Option[String])

object Ingredients {
  def main(args: Array[String]) {
    val line = readLine("Type an ingredient.")
    println(line)
  }
}
