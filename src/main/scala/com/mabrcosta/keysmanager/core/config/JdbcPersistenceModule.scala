package com.mabrcosta.keysmanager.core.config

import com.google.inject.Provides
import com.mabrcosta.keysmanager.core.config.properties.Persistence
import com.mabrcosta.keysmanager.core.persistence.util.DatabaseMigrator
import com.typesafe.config.Config
import javax.inject.Singleton
import net.codingwell.scalaguice.ScalaModule
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

class JdbcPersistenceModule extends ScalaModule {

  override def configure(): Unit = {
    bind[DatabaseMigrator].in(classOf[Singleton])
  }

  @Provides
  @Singleton
  def provideConfig(config: Config): DatabaseConfig[JdbcProfile] =
    DatabaseConfig.forConfig(Persistence.CONFIG_KEY, config)

  @Provides
  @Singleton
  def provideJdbcProfile(dbConfig: DatabaseConfig[JdbcProfile]): JdbcProfile = dbConfig.profile

  @Provides
  @Singleton
  def provideDbJdbcProfile(dbConfig: DatabaseConfig[JdbcProfile]): JdbcProfile#Backend#Database = dbConfig.db

}
