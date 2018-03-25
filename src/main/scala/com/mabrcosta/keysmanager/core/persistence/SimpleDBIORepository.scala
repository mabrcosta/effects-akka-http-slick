package com.mabrcosta.keysmanager.core.persistence

import com.byteslounge.slickrepo.meta.{Entity, Keyed}
import com.byteslounge.slickrepo.repository.Repository
import slick.dbio.{DBIO => SlickDBIO}
import slick.jdbc.JdbcProfile

abstract class SimpleDBIORepository[TEntity <: Entity[TEntity, TKey], TKey](val profile: JdbcProfile)
    extends Repository[TEntity, TKey](profile)
    with DatabaseDal[TEntity, TKey, SlickDBIO] {

  import profile.api._

  abstract class SimpleRepositoryTable(tag: Tag, schema: Option[String], name: String)
      extends Table[TEntity](tag, schema, name)
      with Keyed[TKey] {
    def id = column[TKey]("uid", O.PrimaryKey)
  }

  private lazy val existsCompiled = Compiled((id: Rep[TKey]) => tableQuery.filter(_.id === id).exists)

  override def exists(id: TKey): DBIO[Boolean] = {
    existsCompiled(id).result
  }

  override def find(id: TKey): DBIO[Option[TEntity]] = {
    findOneCompiled(id).result.headOption
  }

  override def find(ids: Seq[TKey]): DBIO[Seq[TEntity]] = {
    tableQuery.filter(_.id inSet ids).result
  }

}
