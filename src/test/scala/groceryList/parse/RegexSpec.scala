package groceryList.parse

import org.specs2.mutable.Specification

/**
 * Created by cfreeman on 7/24/14.
 */
class RegexSpec extends Specification{

  "regex" should {
    val r = "(a|([0-9]+\\s)?([0-9]+)/([0-9]+)|[0-9]?(\\.[0-9]+)?)".r
    val r2 = s"$r (.*)".r
    //val r = "[0-9]+(\\.[0-9]+)?".r

    "work" in {
      val check =
      r2.findFirstMatchIn("4 boneless pork chops")
       match {
        case Some(s) => println(s); s.groupCount + ""
        case _ => ""
      }
      println(check)
      check must not beEmpty
    }
  }
}
