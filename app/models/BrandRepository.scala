package models
 
import javax.inject.{ Inject, Singleton }
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import slick.sql.{ FixedSqlStreamingAction, SqlAction }
import slick.dbio.{ Effect, NoStream }

import scala.concurrent.{ Future, ExecutionContext }

/**
 * A repository for brand.
 *
 * @param dbConfigProvider The Play db config provider. Play will inject this for you.
 */
@Singleton
class BrandRepository @Inject() (dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  // We want the JdbcProfile for this provider
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  // These imports are important, the first one brings db into scope, which will let you do the actual db operations.
  // The second one brings the Slick DSL into scope, which lets you define the table and other queries.
  import dbConfig._
  import profile.api._

  /**
   * Here we define the table. It will have a name of brand
   */
  private class BrandTable(tag: Tag) extends Table[Brand](tag, "brands") {

    /** The ID column, which is the primary key, and auto incremented */
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    /** The name column */
    def name = column[String]("name")

    /** The year column */
    def year = column[Int]("year")

    /**
     * This is the tables default "projection".
     *
     * It defines how the columns are converted to and from the Brand object.
     *
     * In this case, we are simply passing the id, name and page parameters to the Brand case classes
     * apply and unapply methods.
     */
    def * = (id, name, year) <> ((Brand.apply _).tupled, Brand.unapply)
  }

  /**
   * The starting point for all queries on the brand table.
   */
  private val brandTable = TableQuery[BrandTable]

  /**
   * Create a brand with the given name and year.
   *
   * This is an asynchronous operation, it will return a future of the created brand, which can be used to obtain the
   * id for that brand.
   */
  def create(name: String, year: Int): Future[Brand] = db.run {
    // We create a projection of just the name and year columns, since we're not inserting a value for the id column
    (brandTable.map(p => (p.name, p.year))
      // Now define it to return the id, because we want to know what id was generated for the brand
      returning brandTable.map(_.id)
      // And we define a transformation for the returned value, which combines our original parameters with the
      // returned id
      into ((nameYear, id) => Brand(id, nameYear._1, nameYear._2))
      // And finally, insert the brand into the database
      ) += (name, year)
  }

  /**
   * List all the brand in the database.
   */
  def list(): Future[Seq[Brand]] = db.run {
    brandTable.result
  }

  def findById(id: Long): Future[Brand] = db.run {
    brandTable.filter(_.id === id).result.head
  }

  def update(newBrand: Brand, brandId: Option[Long]): Future[Int] = db.run {
    brandTable.filter(_.id === brandId)
      .map(brand => (brand.id, brand.name, brand.year))
      .update((newBrand.id, newBrand.name, newBrand.year))
  }

  def delete(id: Long): Future[Int] =
    db.run {
      // delete from brands where id=1234
      //FIXME:  AND OR Not
      brandTable.filter(_.id === id).delete
    }
 }