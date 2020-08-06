package modules

import com.google.inject.AbstractModule
import play.api.libs.concurrent.AkkaGuiceSupport
import actors.{ConfiguredActor}
import actors.{ConfiguredChildActor, ParentActor}

class MyModule extends AbstractModule with AkkaGuiceSupport {
  println("****My Module get started")
  override def configure = {
    // create actor for ConfiguredActor, with name "configured-actor"
    // and use "configured-actor" to get the reference
    bindActor[ConfiguredActor]("configured-actor")
    // no flexiblity to pass optional arguments to constructor of akka actors
    // no builders
    // actor instance created by AkkaGuiceSupport
    bindActor[ParentActor]("parent-actor")

    // when ConfiguredChildActor needed, a factory is used.
    // pros: control over actor instance creation, manually done via factory
    bindActorFactory[ConfiguredChildActor, ConfiguredChildActor.Factory]
    println("****configured-actor")
  }
}