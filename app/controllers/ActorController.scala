package controllers

import actors.{GetConfig, HelloActor, SayHello}
import play.api.mvc._
import akka.actor._
import javax.inject._
import actors.HelloActor._
import actors.ParentActor.GetChild

import scala.concurrent.duration._
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.ExecutionContext


@Singleton
class ActorController @Inject() (system: ActorSystem,
                                 @Named("configured-actor") configuredActor: ActorRef,
                                 @Named("parent-actor") parentActor: ActorRef,
                                 cc: ControllerComponents) (implicit ec: ExecutionContext
) extends AbstractController(cc) {
  implicit val timeout: Timeout = 5.seconds

  val helloActor = system.actorOf(HelloActor.props, "hello-actor")

  def sayHello(name: String) = Action.async {
    (helloActor ? SayHello(name)).mapTo[String].map { message =>
      Ok(message)
    }
  }

  def getConfig = Action.async {
    (configuredActor ? GetConfig).mapTo[String].map { message =>
      Ok(message)
    }
  }


  def getConfigParent = Action.async {
    (configuredActor ? GetConfig).mapTo[String].map { message =>
      Ok(message)
    }
  }
  //...
}