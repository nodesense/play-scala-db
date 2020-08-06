package v1.review

import javax.inject.Inject

import play.api.Logger
import play.api.data.Form
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

case class ReviewFormInput(title: String, body: String)

/**
 * Takes HTTP requests and produces JSON.
 */
//ReviewControllerComponents can have more DI objects
class ReviewController @Inject()(cc: ReviewControllerComponents)(
  implicit ec: ExecutionContext)
  extends ReviewBaseController(cc) {

  private val logger = Logger(getClass)

  private val form: Form[ReviewFormInput] = {
    import play.api.data.Forms._

    Form(
      mapping(
        "title" -> nonEmptyText,
        "body" -> text
      )(ReviewFormInput.apply)(ReviewFormInput.unapply)
    )
  }

  // /v1/reviews
  // GET /v1/reviews
  def index: Action[AnyContent] = ReviewAction.async { implicit request =>
    logger.trace("index: ")
    reviewResourceHandler.find.map { reviews =>
      Ok(Json.toJson(reviews))
    }
  }

  // POST /v1/reviews

  // http://localhost:9000/v1/reviews
  // {  "title": "Product 5 is too good",      "body": "all good" }

  def process: Action[AnyContent] = ReviewAction.async { implicit request =>
    logger.trace("process: ")
    // we don't pass the request object
    // SCALA compiler sees implicit request => which is Request object
    // SCALA compiler  also processJsonReview method, it has implicit for type Request
    processJsonReview()
  }

  def show(id: String): Action[AnyContent] = ReviewAction.async {
    implicit request =>
      logger.trace(s"show: id = $id")
      reviewResourceHandler.lookup(id).map { review =>
        Ok(Json.toJson(review))
      }
  }

  private def processJsonReview[A]()(
    implicit request: ReviewRequest[A]): Future[Result] = {
    // helper method, you call whaterver name you want
    // fold higher order fucntion, call failure if json form is wrong
    def failure(badForm: Form[ReviewFormInput]) = {
      Future.successful(BadRequest(badForm.errorsAsJson))
    }

    // helper method, you call whaterver name you want
    // fold higher order fucntion, call success if json form is alright
    def success(input: ReviewFormInput) = {
      reviewResourceHandler.create(input).map { review =>
        Created(Json.toJson(review)).withHeaders(LOCATION -> review.link)
      }
    }

    // form will contain {title, body}, shall bind ReviewForm
    form.bindFromRequest().fold(failure, success)
  }
}