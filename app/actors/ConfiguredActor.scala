package actors

import akka.actor._
import javax.inject._
import play.api.Configuration

case object GetConfig;

object ConfiguredActor {

}

class   ConfiguredActor @Inject() (configuration: Configuration) extends Actor {
  import ConfiguredActor._

  val config = configuration.getOptional[String]("my.config").getOrElse("none")

  def receive = {
    case GetConfig =>
      sender() ! config
  }
}