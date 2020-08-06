package actors

import akka.actor._
import javax.inject._
import play.api.Configuration

object OrderActor {
  case object CreateOrder
}

class OrderActor @Inject() (configuration: Configuration) extends Actor {
  import OrderActor._

  val config = configuration.getOptional[String]("my.config").getOrElse("none")

  def receive = {
    case CreateOrder =>
      sender() ! config
  }
}