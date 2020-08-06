package v1.review

import javax.inject.Inject
import play.api.routing.Router.Routes
import play.api.routing.SimpleRouter
import play.api.routing.sird._

/**
 * Routes and URLs to the ReviewResource controller.
 */
class ReviewRouter @Inject()(controller: ReviewController) extends SimpleRouter {
  val prefix = "/v1/reviews"

  def link(id: Int): String = {
    import io.lemonlabs.uri.dsl._
    val url = prefix / id.toString
    url.toString()
  }

  override def routes: Routes = {
    case GET(p"/") =>
      controller.index

    case POST(p"/") =>
      controller.process

    case GET(p"/$id") =>
      controller.show(id)
  }

}