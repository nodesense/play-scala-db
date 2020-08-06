package modules

import com.google.inject.AbstractModule
import play.api.libs.concurrent.AkkaGuiceSupport

class ConfigurationModule extends AbstractModule with AkkaGuiceSupport {

  override def configure = {
    println("****ConfigurationModule configure")
  }
}