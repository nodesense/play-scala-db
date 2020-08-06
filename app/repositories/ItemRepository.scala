package repositories

import javax.inject.{Inject, Singleton}

import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import models.Item
import scala.concurrent.{ExecutionContext, Future}

/**
 * A repository for item.
 *
 * @param dbConfigProvider The Play db config provider. Play will inject this for you.
 */
@Singleton
class ItemRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  // We want the JdbcProfile for this provider
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  // These imports are important, the first one brings db into scope, which will let you do the actual db operations.
  // The second one brings the Slick DSL into scope, which lets you define the table and other queries.
  import dbConfig._
  import profile.api._

  /**
   * Here we define the table. It will have a name of item
   */
  private class ItemTable(tag: Tag) extends Table[Item](tag, "items") {

    /** The ID column, which is the primary key, and auto incremented */
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def order_id = column[Long]("order_id")
    def product_id = column[Long]("product_id")
    def brand_id = column[Long]("brand_id")
    def price = column[Int]("price")
    def qty = column[Int]("qty")
    def amount = column[Int]("amount")

    def * = (id, order_id, product_id, brand_id, price, qty, amount) <> ((Item.apply _).tupled, Item.unapply)
  }

  private val itemTable = TableQuery[ItemTable]

  def create(order_id: Long, product_id: Long, brand_id: Long,  price: Int, qty: Int, amount: Int): Future[Item] = db.run {
    (itemTable.map(p => (p.order_id, p.product_id, p.brand_id, p.price, p.qty, p.amount))
      returning itemTable.map(_.id)
      into ((itemTuple, id) => Item(id, itemTuple._1, itemTuple._2, itemTuple._3, itemTuple._4, itemTuple._5, itemTuple._6))
      ) += (order_id, product_id, brand_id, price, qty, amount)
  }

  def list(): Future[Seq[Item]] = db.run {
    itemTable.result
  }

  def query(): Future[Seq[Item]] = db.run {
    itemTable.result
  }



  def findById(id: Long): Future[Item] = db.run {
    itemTable.filter(_.id === id).result.head
  }

  def delete(id: Long): Future[Int] =
    db.run {
      // delete from items where id=1234
      //FIXME:  AND OR Not
      itemTable.filter(_.id === id).delete
    }
}