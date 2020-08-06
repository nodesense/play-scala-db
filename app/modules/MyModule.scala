package modules

import com.google.inject.AbstractModule
import play.api.libs.concurrent.AkkaGuiceSupport
import actors.{ConfiguredActor}
import actors.{ConfiguredChildActor, ParentActor}

class MyModule extends AbstractModule with AkkaGuiceSupport {
  println("****My Module get started")
  override def configure = {
    bindActor[ConfiguredActor]("configured-actor")
    bindActor[ParentActor]("parent-actor")

   bindActorFactory[ConfiguredChildActor, ConfiguredChildActor.Factory]

    println("****configured-actor")

  }
}