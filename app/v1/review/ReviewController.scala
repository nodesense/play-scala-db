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

  def index: Action[AnyContent] = ReviewAction.async { implicit request =>
    logger.trace("index: ")
    reviewResourceHandler.find.map { reviews =>
      Ok(Json.toJson(reviews))
    }
  }

  def process: Action[AnyContent] = ReviewAction.async { implicit request =>
    logger.trace("process: ")
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
    def failure(badForm: Form[ReviewFormInput]) = {
      Future.successful(BadRequest(badForm.errorsAsJson))
    }

    def success(input: ReviewFormInput) = {
      reviewResourceHandler.create(input).map { review =>
        Created(Json.toJson(review)).withHeaders(LOCATION -> review.link)
      }
    }

    form.bindFromRequest().fold(failure, success)
  }
}