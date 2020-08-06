package actors

import akka.actor._
import javax.inject._
import play.api.libs.concurrent.InjectedActorSupport

object ParentActor {
  case class GetChild(key: String)
}

class ParentActor @Inject() (
                              childFactory: ConfiguredChildActor.Factory
                            ) extends Actor
  with InjectedActorSupport {
  import ParentActor._

  val childActor1: ActorRef = injectedChild(childFactory("child-actor-1"), "child-actor-1")
//  val childActor2: ActorRef = injectedChild(childFactory("child-actor-2"), "child-actor-2")

  def receive = {
    case GetChild(key: String) =>
      val child: ActorRef = injectedChild(childFactory(key), key)
      sender() ! child
    case GetConfig =>  childActor1.forward(GetConfig)

  }
}