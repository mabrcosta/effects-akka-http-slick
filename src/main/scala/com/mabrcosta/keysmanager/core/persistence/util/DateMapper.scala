package com.mabrcosta.keysmanager.core.persistence.util

import java.sql.Timestamp
import java.time._

import slick.ast.BaseTypedType
import slick.jdbc.{JdbcProfile, JdbcType}

trait DateMapper {
  implicit val profile: JdbcProfile
  import profile.api._

  object DateMapper {

    val instant2SqlTimestampMapper: JdbcType[Instant] with BaseTypedType[Instant] =
      MappedColumnType.base[Instant, java.sql.Timestamp](
        { instant =>
          Timestamp.valueOf(LocalDateTime.ofInstant(instant, ZoneId.of("UTC")))
        }, { sqlTimestamp =>
          ZonedDateTime.of(sqlTimestamp.toLocalDateTime, ZoneId.of("UTC")).toInstant
        }
      )

  }
}
