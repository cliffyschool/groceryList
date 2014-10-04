package groceryList.model

/**
 * Created by cfreeman on 7/29/14.
 */
trait UnitOfMeasure {
  val name:String
  def known:Boolean
}

case class UnknownUnitOfMeasure(name: String) extends UnitOfMeasure{
  override def known = false
}

case class KnownUnitOfMeasure (name: String) extends UnitOfMeasure{
  override def known = true
}
