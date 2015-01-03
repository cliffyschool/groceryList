package domain

trait ListRepository {
  def getAll : Seq[List]
  def findById(id: String) : Option[List]
  def save(id: String, list: List)

}
