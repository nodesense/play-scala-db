package filters

import akka.stream.Materializer
import javax.inject.Inject
import play.api.mvc.{Filter, RequestHeader, Result}

import scala.concurrent.{ExecutionContext, Future}

// Performance monitor
// start of the request
// controller/routers etc
// end time of the response
// millis = end time - start time  (time took to execute request)

class LoggingFilter @Inject() (implicit val mat: Materializer, ec: ExecutionContext) extends Filter {


  // middleware

  // request -> filter 1 -> filter 2 -> filter -> router/controllers
  // nextFilter a function
  // filter 1 apply fun {
  //     startTime = now()
  //   filter 2 appply fun {
//         filter 3 apply {
//            at end of the filter,
//            call router/controller
//   endTime = now()
//     val time = endTime - startTime
  // }

  def apply(nextFilter: RequestHeader => Future[Result])
           (requestHeader: RequestHeader): Future[Result] = {

    val startTime = System.currentTimeMillis

    // next next filter in the line
    // result is Ok, NotFound, Result object
    nextFilter(requestHeader).map { result =>

      val endTime = System.currentTimeMillis
      val requestTime = endTime - startTime

      println(s"**Logging Filter ${requestHeader.method} ${requestHeader.uri} took ${requestTime}ms and returned ${result.header.status}");
      // Logger.info()

      // add header, optional
      result.withHeaders("Request-Time" -> requestTime.toString)
    }
  }
}