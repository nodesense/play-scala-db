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


// good practice to use actors in play controller is to inject them
// configure Actors in Module with bind/bindActor and inject here
// all controllers will have single actor reference
//@Named("configured-actor") is configured in MyModule
// actor is not created per controller

@Singleton
class ActorController @Inject() (system: ActorSystem,
                                 @Named("configured-actor") configuredActor: ActorRef,
                                 @Named("parent-actor") parentActor: ActorRef,
                                 cc: ControllerComponents) (implicit ec: ExecutionContext
) extends AbstractController(cc) {
  implicit val timeout: Timeout = 5.seconds

  // actor creation
  // only one actor by name is allowed, we need single actor for helloactor
  // created whenever ActorController is created
  // if HelloActor is not singleton, then this will create more instances, error, not a good one
  // good practise is not to have controller create actors
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