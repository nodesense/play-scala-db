package v1.review

import javax.inject.{Inject, Singleton, Provider}
import akka.actor.ActorSystem
import models.{Review}
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.concurrent.CustomExecutionContext
import play.api.{Logger, MarkerContext}
import slick.jdbc.JdbcProfile

import scala.concurrent.Future


class ReviewExecutionContext @Inject()(actorSystem: ActorSystem)
  extends CustomExecutionContext(actorSystem, "repository.dispatcher")

/**
 * A pure non-blocking interface for the ReviewRepository.
 */
trait ReviewRepository {
  def create(data: Review)(implicit mc: MarkerContext): Future[Int]

  def list()(implicit mc: MarkerContext): Future[Iterable[Review]]

  def get(id: Int)(implicit mc: MarkerContext): Future[Option[Review]]
  def findById(id: Int): Future[Review]

  def create(id: Int, title: String, body: String): Future[Review]
}

/**
 * A trivial implementation for the Review Repository.
 *
 * A custom execution context is used here to establish that blocking operations should be
 * executed in a different thread than Play's ExecutionContext, which is used for CPU bound tasks
 * such as rendering.
 */
@Singleton
class ReviewRepositoryImpl @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ReviewExecutionContext)
  extends ReviewRepository {

  private val logger = Logger(this.getClass)

  private val reviewList = List(
    Review(1, "title 1", "blog review 1"),
    Review(2, "title 2", "blog review 2"),
    Review(3, "title 3", "blog review 3"),
    Review(4, "title 4", "blog review 4"),
    Review(5, "title 5", "blog review 5")
  )

//  override def list()(
//    implicit mc: MarkerContext): Future[Iterable[Review]] = {
//    Future {
//      logger.trace(s"list: ")
//      reviewList
//    }
//  }
//
  override def get(id: Int)(
    implicit mc: MarkerContext): Future[Option[Review]] = {
    Future {
      logger.trace(s"get: id = $id")
      reviewList.find(review => review.id == id)
    }
  }

  def create(data: Review)(implicit mc: MarkerContext): Future[Int] = {
    Future {
      logger.trace(s"create: data = $data")
      data.id
    }
  }







  // We want the JdbcProfile for this provider
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  // These imports are important, the first one brings db into scope, which will let you do the actual db operations.
  // The second one brings the Slick DSL into scope, which lets you define the table and other queries.
  import dbConfig._
  import profile.api._

  /**
   * Here we define the table. It will have a name of review
   */
  private class ReviewTable(tag: Tag) extends Table[Review](tag, "reviews") {

    /** The ID column, which is the primary key, and auto incremented */
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)

    /** The title column */
    def title = column[String]("title")

    def body = column[String]("body")

    /**
     * This is the tables default "projection".
     *
     * It defines how the columns are converted to and from the Review object.
     *
     * In this case, we are simply passing the id, name and page parameters to the Review case classes
     * apply and unapply methods.
     */
    def * = (id, title, body) <> ((Review.apply _).tupled, Review.unapply)
  }

  /**
   * The starting point for all queries on the review table.
   */
  private val reviewTable = TableQuery[ReviewTable]

  /**
   * Create a review with the given name and year.
   *
   * This is an asynchronous operation, it will return a future of the created review, which can be used to obtain the
   * id for that review.
   */
  override def create(id: Int, title: String, body: String): Future[Review] = db.run {
    (reviewTable.map(p => (p.title, p.body))
      returning reviewTable.map(_.id)
      into ((reviewTuple, id) => Review(id, reviewTuple._1, reviewTuple._2))
      // And finally, insert the review into the database
      ) += (title, body)
  }

  override def list()(
    implicit mc: MarkerContext): Future[Iterable[Review]] = db.run {
    reviewTable.result
  }


  /**
   * List all the review in the database.
   */
//  def list(): Future[Seq[Review]] = db.run {
//    reviewTable.result
//  }
  //
  //  query.filter {
  //    m => conditions.map(n => m._13 === n._1 && m._14 === n._2).reduceLeft(_ || _)
  //  }

  def query(): Future[Seq[Review]] = db.run {
    reviewTable.result
  }

  //  def findAll(userId: Long, limit: Int, offset: Int) = db.run{
  //    reviewTable.filter(_.id === userId).drop(offset).take(limit).result
  //  }


  override def findById(id: Int): Future[Review] = db.run {
    reviewTable.filter(_.id === id).result.head
  }

  def  findAll(brandId: Long, price: Int,  offset: Int, limit: Int ): Future[Seq[Review]] = db.run{
    reviewTable.result
  }

  def delete(id: Int): Future[Int] =
    db.run {
      // delete from reviews where id=1234
      //FIXME:  AND OR Not
      reviewTable.filter(_.id === id).delete
    }

}