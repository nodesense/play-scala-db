package actors

import akka.actor._

case class SayHello(name: String)
// untyped actor, receive any messages, no compilation error
// if message not processed, it goes to case _, may be unnoticed
class HelloActor extends  Actor {
  def receive = {
    // this actor respond back to the sender
    // for the ask query
    // ! means tell another actor, fire and forget
    case SayHello(name: String) => sender() ! s"Howdy, $name"
    case _ => "I won't say hello"
  }
}

object HelloActor {
  def props = Props[HelloActor]
}