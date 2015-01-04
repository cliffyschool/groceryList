package domain

trait ListRepository {
  def getAll : Seq[List]
  def findById(id: String) : Option[List]
  def save(id: String, list: List)
}

class StubListRepository extends ListRepository{

  var lists = Map[String,List]()

  override def getAll: Seq[List] = lists.values.toSeq

  override def findById(id: String): Option[List] = lists.get(id)

  override def save(id: String, list: List): Unit = lists = lists + (id -> list)
}
