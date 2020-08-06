package v1.review

import javax.inject.Inject
import play.api.routing.Router.Routes
import play.api.routing.SimpleRouter
import play.api.routing.sird._

/**
 * Routes and URLs to the ReviewResource controller.
 */

// instaed of mapping routes to controller, it goes via router
// GET /v1/reviers (Rotuer) --> Controller
class ReviewRouter @Inject()(controller: ReviewController) extends SimpleRouter {
  val prefix = "/v1/reviews"

  def link(id: Int): String = {
    import io.lemonlabs.uri.dsl._
    val url = prefix / id.toString
    url.toString()
  }

  // get called for GET/PUT,POST etc methods
  // partial functions
  override def routes: Routes = {
    // GET /v1/reviews/
    case GET(p"/") =>
      // call index method
      controller.index

      // POST /v1/reviews/
    case POST(p"/") =>
      controller.process

      ///GET /v1/reviews/123232
    case GET(p"/$id") =>
      controller.show(id)
  }

}