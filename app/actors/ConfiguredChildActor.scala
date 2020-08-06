package actors

import akka.actor._
import javax.inject._
import com.google.inject.assistedinject.Assisted
import play.api.Configuration

object ConfiguredChildActor {
 // case object GetConfig

  trait Factory {
    def apply(key: String): Actor
  }
}

class ConfiguredChildActor @Inject() (configuration: Configuration, @Assisted key: String) extends Actor {
  import ConfiguredChildActor._

  println("****-- key is ", key)

  val config = configuration.getOptional[String](key).getOrElse("none")

  def receive = {
    case GetConfig =>
      sender() ! config
  }
}