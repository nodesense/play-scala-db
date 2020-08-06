package repositories

import javax.inject.{Inject, Singleton}
import models.{PaginatedProductResult, Product}
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json.Json
import slick.jdbc.JdbcProfile
import slick.sql.{FixedSqlStreamingAction, SqlAction}
import slick.dbio.{Effect, NoStream}

import scala.concurrent.{ExecutionContext, Future}

/**
 * A repository for product.
 *
 * @param dbConfigProvider The Play db config provider. Play will inject this for you.
 */
@Singleton
class ProductRepository @Inject() (dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  // We want the JdbcProfile for this provider
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  // These imports are important, the first one brings db into scope, which will let you do the actual db operations.
  // The second one brings the Slick DSL into scope, which lets you define the table and other queries.
  import dbConfig._
  import profile.api._

  /**
   * Here we define the table. It will have a name of product
   */
  private class ProductTable(tag: Tag) extends Table[Product](tag, "products") {

    /** The ID column, which is the primary key, and auto incremented */
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    /** The name column */
    def name = column[String]("name")

    /** The year column */
    def price = column[Int]("price")

    def brand_id = column[Long]("brand_id")

    /**
     * This is the tables default "projection".
     *
     * It defines how the columns are converted to and from the Product object.
     *
     * In this case, we are simply passing the id, name and page parameters to the Product case classes
     * apply and unapply methods.
     */
    def * = (id, name, price, brand_id) <> ((Product.apply _).tupled, Product.unapply)
  }

  /**
   * The starting point for all queries on the product table.
   */
  private val productTable = TableQuery[ProductTable]

  /**
   * Create a product with the given name and year.
   *
   * This is an asynchronous operation, it will return a future of the created product, which can be used to obtain the
   * id for that product.
   */
  def create(name: String, price: Int, brand_id: Long): Future[Product] = db.run {
    // We create a projection of just the name and year columns, since we're not inserting a value for the id column
    (productTable.map(p => (p.name, p.price, p.brand_id))
      // Now define it to return the id, because we want to know what id was generated for the product
      returning productTable.map(_.id)
      // And we define a transformation for the returned value, which combines our original parameters with the
      // returned id
      into ((productTuple, id) => Product(id, productTuple._1, productTuple._2, productTuple._3))
      // And finally, insert the product into the database
      ) += (name, price, brand_id)
  }

  /**
   * List all the product in the database.
   */
  def list(): Future[Seq[Product]] = db.run {
    productTable.result
  }
  //
  //  query.filter {
  //    m => conditions.map(n => m._13 === n._1 && m._14 === n._2).reduceLeft(_ || _)
  //  }

  def query(): Future[Seq[Product]] = db.run {
    productTable.result
  }

//  def findAll(userId: Long, limit: Int, offset: Int) = db.run{
//    productTable.filter(_.id === userId).drop(offset).take(limit).result
//  }

  def findById(id: Long): Future[Product] = db.run {
    productTable.filter(_.id === id).result.head
  }

  def update(newProduct: Product, productId: Option[Long]): Future[Int] = db.run {
    productTable.filter(_.id === productId)
      .map(product => (product.id, product.name, product.price))
      .update((newProduct.id, newProduct.name, newProduct.price))
  }

  def  findAll(brandId: Long, price: Int,  offset: Int, limit: Int ): Future[Seq[Product]] = db.run{
    //productTable.result
    // productTable.filter( product => (product.brand_id === brandId)).result
    // and &&
    // or ||
    //productTable.filter( product => (product.brand_id === brandId && product.price >= price)).result

    productTable
        .filter( product => (product.brand_id === brandId || product.price >= price))
          .drop(offset) // skip first few records
          .take(limit) // LIMIT 10
        .result

    // productTable.filter( product => (product.brand_id  === brandId && product.price > price)).result
    // productTable.filter( product => (product.brand_id  === brandId && product.price > 100)
    //                    || (product.price > 1000)).result
     //                    || (product.price > 1000)).result

    // productTable.filter( product => (product.brand_id  === brandId)).result
  }


  // {products: [], totalProducts: 100, hasNextPage: true/false}

  def  findAll2(brandId: Long, price: Int,  offset: Int, limit: Int ): Future[PaginatedProductResult] = db.run{

      for {
        products <-  productTable.filter( product => (product.brand_id  === brandId || product.price > price))
          .sortBy(_.price)
          .drop(offset).take(limit)
          .result

        totalProducts <- productTable.filter(product => (product.brand_id  === brandId || product.price > price)).length.result
      } yield PaginatedProductResult(
        totalCount = totalProducts,
        products = products.toList,
        hasNextPage = totalProducts - (offset + limit) > 0
      )
    }

    //productTable.filter( product => (product.brand_id  === brandId && product.price > price)).result
    // productTable.filter( product => (product.brand_id  === brandId && product.price > 100)
    //                    || (product.price > 1000)).result
    //                    || (product.price > 1000)).result

    // productTable.filter( product => (product.brand_id  === brandId)).result


  def delete(id: Long): Future[Int] =
    db.run {
      // delete from products where id=1234
      //FIXME:  AND OR Not
      productTable.filter(_.id === id).delete
    }
}