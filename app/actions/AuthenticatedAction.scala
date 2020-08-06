package actions

import play.api.mvc.{Request, WrappedRequest}
import javax.inject.Inject
import play.api.Logging
import play.api.http.HttpEntity
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}


class AuthenticatedRequest[A] (val username: String, val password: String, val request: Request[A]) extends WrappedRequest[A](request)

class AuthenticatedAction @Inject() (parser: BodyParsers.Default)(implicit ec: ExecutionContext)
  extends ActionBuilderImpl(parser)
    with Logging {

  override def invokeBlock[A](request: Request[A], block: (Request[A]) => Future[Result]) = {

    val optionalCredentialsTuple = parseUserCredentials(request)
    optionalCredentialsTuple.map { credentials =>
      block(new actions.AuthenticatedRequest(credentials._1, credentials._2, request))
    } getOrElse Future.successful( Result(
      header = ResponseHeader(403, Map.empty),
      body = HttpEntity.Strict(akka.util.ByteString("forbidden"), Some("text/plain"))
    ))
  }

  def parseUserCredentials(request: RequestHeader): Option[(String, String)] = {
    Some(("test", "krish"))
    //    val query = request.queryString.map { case(k, v) => k -> v.mkString }
    //    for {
    //      user <- query.get("username")
    //      pass <- query.get("password")
    //    } yield(user, pass)
    //  }
  }
}