package com.mabrcosta.keysmanager

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.google.inject.Guice
import com.mabrcosta.keysmanager.core.config._
import com.mabrcosta.keysmanager.core.data.ServerConfiguration
import com.mabrcosta.keysmanager.core.persistence.util.DatabaseMigrator
import com.mabrcosta.keysmanager.users.key.rest.KeysHttpService
import com.typesafe.scalalogging.LazyLogging

object KeysManager extends App with LazyLogging {
  try {
    val injector = Guice.createInjector(
      new AkkaModule,
      new ConfigModule,
      new JdbcPersistenceModule,
      new KeysManagerModule
    )

    import net.codingwell.scalaguice.InjectorExtensions._

    implicit val system: ActorSystem = injector.instance[ActorSystem]
    implicit val materializer: ActorMaterializer = injector.instance[ActorMaterializer]

    injector.instance[DatabaseMigrator]

    val restService = injector.instance[KeysHttpService]
    val configuration = injector.instance[ServerConfiguration]

    Http().bindAndHandle(restService.routes, "0.0.0.0", port = configuration.port)
  } catch {
    case e: Throwable => logger.error(e.getMessage, e)
  }
}
