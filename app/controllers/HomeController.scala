package controllers

import actions.LoggingAction
import javax.inject._
import play.api._
import play.api.http.HttpEntity
import play.api.mvc._

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(loggingAction: LoggingAction, val controllerComponents: ControllerComponents) extends BaseController  with Logging {

  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index())
  }

  // action, this called when a url GET/POSt.. invoked
  // configured in routes
  def page1() = Action {
    Ok("Page 1") /// 200 OK
  }

  // 301 moved to /page1
  def page2() = Action {
    Redirect("/page1")
  }

  // 404 not found
  def page4() = Action {
    // Content-Type: text/html if not it will be text/plain
    NotFound("<h2>Page 4 not found</h2>").as("text/html")
  }

  def crash() = Action {
    // 5xx error
    InternalServerError("Unexpected exception")
  }

  def statusCode() = Action {
    Status(403) ("Forbidden error")
  }

  def result() = Action {
    Result(
      header = ResponseHeader(200, Map.empty),
      body = HttpEntity.Strict(akka.util.ByteString("Hello World"), Some("text/plain"))
    )
  }

  // TODO, not yet implemented
  def comingSoon() = TODO

  // URL parameters, part of the route itself
  // /page/:pageId/:slug
  // /page/12345/how-to-akka

  def servePage(id: String) = Action {
    Ok( s"Here your page $id")
  }

  // q - query string, is an optional, Option[String]
  // search/page?q="iphone"

  def searchPageByName(q: Option[String]) = Action {

    Ok(s"here are listing for search query $q")
  }

  // generic query without action parameters
  // by using request

  // /query?q=mobile&skip=2&limit=100
  def genericQuery() = Action {
    implicit request: Request[AnyContent] =>
      val q = request.getQueryString("q");
      val skip = request.getQueryString(("skip"))
      val limit = request.getQueryString("limit")

      Ok(s"query $q, skip $skip, limit $limit")
  }

  def logAction = loggingAction {
    Ok("Hello World")
  }

  def submit = loggingAction(parse.text) { request =>
     logger.warn("Welcome submit***")
     Ok("Got a body " + request.body.length + " bytes long")
  }
}
