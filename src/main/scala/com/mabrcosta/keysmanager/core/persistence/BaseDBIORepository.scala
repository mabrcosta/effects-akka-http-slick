package com.mabrcosta.keysmanager.core.persistence

import java.time.Instant
import java.util.UUID

import com.byteslounge.slickrepo.meta.Entity
import com.mabrcosta.keysmanager.core.persistence.util.DateMapper
import slick.ast.BaseTypedType
import slick.jdbc.{JdbcProfile, JdbcType}

abstract class BaseDBIORepository[TEntity <: Entity[TEntity, TKey], TKey](
    override val profile: JdbcProfile)
    extends SimpleDBIORepository[TEntity, TKey](profile) with DateMapper {

  import profile.api._

  abstract class BaseRepositoryTable(tag: Tag, schema: Option[String] = None, name: String)
      extends SimpleRepositoryTable(tag, schema, name) {

    implicit val instantMapper: JdbcType[Instant] with BaseTypedType[Instant] = DateMapper.instant2SqlTimestampMapper

    def uidCreatorSubject = column[Option[UUID]]("uid_creator_subject")
    def uidLastModifierSubject = column[Option[UUID]]("uid_last_modifier_subject")
    def creationTimestamp = column[Instant]("creation_timestamp")
    def updateTimestamp = column[Instant]("update_timestamp")
  }

}
