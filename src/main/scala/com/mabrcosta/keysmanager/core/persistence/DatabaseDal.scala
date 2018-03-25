package com.mabrcosta.keysmanager.core.persistence

import com.byteslounge.slickrepo.meta.Entity

import scala.concurrent.ExecutionContext

trait DatabaseDal[TEntity <: Entity[TEntity, TKey], TKey, TIO[_]] {

  def count(): TIO[Int]

  def exists(id: TKey): TIO[Boolean]

  def findAll()(implicit ec: ExecutionContext): TIO[Seq[TEntity]]
  def find(id: TKey): TIO[Option[TEntity]]
  def find(ids: Seq[TKey]): TIO[Seq[TEntity]]

  def save(entity: TEntity)(implicit ec: ExecutionContext): TIO[TEntity]

  def update(entity: TEntity)(implicit ec: ExecutionContext): TIO[TEntity]

  def delete(entity: TEntity)(implicit ec: ExecutionContext): TIO[TEntity]

  def lock(entity: TEntity)(implicit ec: ExecutionContext): TIO[TEntity]

}
