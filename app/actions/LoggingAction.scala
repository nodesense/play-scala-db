package actions

import javax.inject.Inject
import play.api.Logging
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

class LoggingAction @Inject() (parser: BodyParsers.Default)(implicit ec: ExecutionContext)
  extends ActionBuilderImpl(parser)
    with Logging {
  // request, client http request
  // block is the block used in LoggingAction { Ok("Hello") }
  override def invokeBlock[A](request: Request[A], block: (Request[A]) => Future[Result]) = {
    // pre-hook
    logger.warn("***LoggingAction  invokeBlock Calling action...begin")
    val result = block(request)
    // post hook
    logger.warn("***LoggingAction  invokeBlock Calling action...end")
    result // Result object, Ok, NotFound etc
  }
}

// Action are block statement, by name convention
