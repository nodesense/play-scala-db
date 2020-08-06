package v1.review

import javax.inject.{Inject, Provider}
import models.{Review}
import play.api.MarkerContext

import scala.concurrent.{ExecutionContext, Future}
import play.api.libs.json._

/**
 * DTO for displaying review information.
 */
case class ReviewResource(id: String, link: String, title: String, body: String)

object ReviewResource {
  /**
   * Mapping to read/write a ReviewResource out as a JSON value.
   */
  implicit val format: Format[ReviewResource] = Json.format
}

/**
 * Controls access to the backend data, returning [[ReviewResource]]
 */

// ReviewRepository is an trait/interface
// ReviewRepositoryImpl is an implementation for DB, reviews table
class ReviewResourceHandler @Inject()(
                                     routerProvider: Provider[ReviewRouter],
                                     reviewRepository: ReviewRepository)(implicit ec: ExecutionContext) {

  def create(reviewInput: ReviewFormInput)(
    implicit mc: MarkerContext): Future[ReviewResource] = {
    // create returns Review which is DB object
    // transfer Review [raw db object] to ReviewResource [enriched, url etc]
    reviewRepository.create(-1, reviewInput.title, reviewInput.body)
      .map {
        review => createReviewResource(review)
      }

//    val data = Review(999, reviewInput.title, reviewInput.body)
//    // We don't actually create the review, so return what we have
//    reviewRepository.create(data).map { id =>
//      createReviewResource(data)
//    }
  }

  def lookup(id: String)(
    implicit mc: MarkerContext): Future[Option[ReviewResource]] = {
    val reviewFuture = reviewRepository.get(id.toInt)
    reviewFuture.map { maybeReviewData =>
      maybeReviewData.map { reviewData =>
        createReviewResource(reviewData)
      }
    }
  }


  def find(implicit mc: MarkerContext): Future[Iterable[ReviewResource]] = {
    reviewRepository.list().map { reviewDataList =>
      reviewDataList.map(reviewData => createReviewResource(reviewData))
    }
  }

  //Review is db object
  // ReviewResource is an response json
  private def createReviewResource(p: Review): ReviewResource = {
    ReviewResource(p.id.toString, routerProvider.get.link(p.id), p.title, p.body)
  }

}