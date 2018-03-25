package com.mabrcosta.keysmanager.core.config

import akka.actor.{ActorRefFactory, ActorSystem}
import akka.stream.ActorMaterializer
import com.google.inject.{Injector, Provides}
import com.mabrcosta.keysmanager.core.config.util.GuiceAkkaExtension
import javax.inject.{Provider, Singleton}
import net.codingwell.scalaguice.ScalaModule

import scala.concurrent.ExecutionContext

/**
  * This module defines the bindings required to support Guice injectable Akka actors.
  * It is a core module required to bootstrap the spray router for rest support.
  *
  * This includes:
  *
  * - ActorSystem      - a singleton instance of the root actor system
  * - ActorRefFactory  - the same instance bound as a ActorRefFactory.  (Guice will
  *                      only inject exact type matches, so we must bind the
  *                      actor system to ActorRefFactory even though ActorSystem
  *                      extends ActorRefFactory).
  * - ExecutionContext - a singleton instance of the execution context provided
  *                      by the root actor system.
  */
class AkkaModule extends ScalaModule {

  override def configure(): Unit = {
  }

  /**
    * Provides the singleton root-actor-system to be injected whenever an ActorSystem
    * is required.  This method also registers the GuiceAkkaExtension
    * to be used for instantiating guice injected actors.
    */
  @Provides
  @Singleton
  def provideActorSystem(injector: Injector): ActorSystem = {
    val system = ActorSystem("keysmanagerActorSystem")
    // initialize and register extension to allow akka to create actors using Guice
    GuiceAkkaExtension(system).initialize(injector)
    system
  }

  /**
    * Provides a singleton factory to be injected whenever an ActorRefFactory
    * is required.
    */
  @Provides
  @Singleton
  def provideActorRefFactory(systemProvider: Provider[ActorSystem]): ActorRefFactory = {
    systemProvider.get
  }

  /**
    * Provides a singleton execution context to be injected whenever an ExecutionContext
    * is required.
    */
  @Provides
  @Singleton
  def provideExecutionContext(systemProvider: Provider[ActorSystem]): ExecutionContext = {
    systemProvider.get.dispatcher
  }

  @Provides
  @Singleton
  def provideActorMaterializer(systemProvider: Provider[ActorSystem]): ActorMaterializer = {
    ActorMaterializer.create(systemProvider.get)
  }

}
