package com.mabrcosta.keysmanager.core.persistence.util

import java.io.IOException
import java.util.Properties

import com.typesafe.scalalogging.LazyLogging
import javax.inject.Inject
import org.flywaydb.core.Flyway
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

case class DatabaseMigratorInfo(schema: String, locations: Seq[String])

class DatabaseMigrator @Inject()(migrators: Set[DatabaseMigratorInfo],
                                 databaseConfig: DatabaseConfig[JdbcProfile])
    extends LazyLogging {

  private val PROPERTIES_FILE_NAME = "DatabaseMigrator.properties";

  def migrate(info: DatabaseMigratorInfo) {
    val flyway = new Flyway()

    val properties = new Properties()
    try {
      properties.load(
        this.getClass.getClassLoader
          .getResourceAsStream(PROPERTIES_FILE_NAME))
    } catch {
      case e: IOException => {
        logger.error("Could not configure database migrator", e)
        throw e
      }
    }
    flyway.configure(properties)

    val persistConfigs = databaseConfig.config
    println(persistConfigs)

    flyway.setSchemas(info.schema)
    flyway.setLocations(info.locations: _*)
    flyway.setDataSource(persistConfigs.getString("db.url"),
                         persistConfigs.getString("db.user"),
                         persistConfigs.getString("db.password"))

    flyway.migrate()
  }

  migrators.foreach(migrate)
}
