import javax.inject._
import com.google.inject.AbstractModule
import net.codingwell.scalaguice.ScalaModule
import play.api.{Configuration, Environment}
import v1.review._

/**
 * Sets up custom components for Play.
 *
 * https://www.playframework.com/documentation/latest/ScalaDependencyInjection
 */
class Module(environment: Environment, configuration: Configuration)
  extends AbstractModule
    with ScalaModule {

  override def configure() = {
    // bind is useful for DI
    // we bind Trait/interface with an implementation /class
    // abstraction, seperate implementation from interface

    // ReviewRepositoryImpl is an implementation of DB
    // ReviewRepositoryELKImpl is an implementation of ELK

    println("****Module configure")

    bind[ReviewRepository].to[ReviewRepositoryImpl].in[Singleton]
    // or  bind[ReviewRepository].to[ReviewRepositoryELKImpl].in[Singleton]
    // or  bind[ReviewRepository].to[ReviewRepositoryProxyStubImpl].in[Singleton]

  }
}