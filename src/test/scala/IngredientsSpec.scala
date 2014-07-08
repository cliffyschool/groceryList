import org.specs2.mutable.Specification

/**
 * Created by U6017873 on 7/6/2014.
 */
class IngredientsSpec extends Specification {

  val ingredients = new Ingredients
  "fromLine" should {
    "handle a 3-part ingredient" in {
      ingredients.fromLine("2 cups butter") must beSome(Ingredient("butter", 2, "cups"))
    }

    "handle a 3-part ingredient with 'of'" in {
      ingredients.fromLine("2 cups of butter") must beSome(Ingredient("butter", 2, "cups"))
    }

    "handle a 3-part ingredient with 'a'" in {
      ingredients.fromLine("a cup of butter") must beSome(Ingredient("butter", 1, "cups"))
    }

    "handle an ingredient with fractions" in {
      ingredients.fromLine("1/2 c. butter") must beSome(Ingredient("butter", 0.5, "cups"))
    }

    "add amounts for like ingredients" in {
      val i1 = new Ingredient("salt", 1, "tbsp")
      val i2 = new Ingredient("salt", 2, "tablespoons")
      val i3 = new Ingredient("salt", 19, "tbsp.")
      val shouldBe = Seq(Ingredient("salt", 22, "tablespoons"))
      ingredients.makeList(Seq(i1), Seq(i2, i3)) must equalTo(shouldBe)
    }

    "add amounts for unlike ingredients" in {
      val i1 = new Ingredient("salt", 1, "tbsp")
      val i2 = new Ingredient("butter", 2, "cups")
      val i3 = new Ingredient("salt", 3, "tbsp.")
      val shouldBe = Seq(Ingredient("salt", 4, "tablespoons"), Ingredient("butter", 2, "cups"))
      ingredients.makeList(Seq(i1), Seq(i2, i3)) must equalTo(shouldBe)
    }

    "add only like units" in {
      val i1 = new Ingredient("salt", 1, "tbsp")
      val i2 = new Ingredient("salt", 2, "cups")
      val i3 = new Ingredient("salt", 3, "tbsp.")
      val shouldBe = Seq(Ingredient("salt", 4, "tablespoons"), Ingredient("salt", 2, "cups"))
      ingredients.makeList(Seq(i1), Seq(i2, i3)) must haveSize(2)
    }
  }
}
