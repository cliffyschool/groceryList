package domain

trait UnitOfMeasure {
  val name:String
  def known:Boolean
}

case class UnknownUnitOfMeasure(name: String) extends UnitOfMeasure{
  override def known = false
}

case class WellKnownUnitOfMeasure (name: String) extends UnitOfMeasure{
  override def known = true
}
