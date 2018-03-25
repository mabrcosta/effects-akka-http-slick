package com.mabrcosta.keysmanager.core.persistence.util

import javax.inject.Inject
import org.atnos.eff.{Eff, FutureEffect, TimedFuture}
import slick.dbio.DBIO
import slick.jdbc.JdbcProfile



class EffDbExecutorDBIOFuture @Inject()(implicit val profile: JdbcProfile,
                                         implicit val db: JdbcProfile#Backend#Database)
  extends EffectsDatabaseExecutor[DBIO, TimedFuture] {

  implicit def apply[T](action: DBIO[T]): EffectsDatabaseActionExecutor[DBIO, TimedFuture, T] =
    new EffDbActionExecutorDBIOFuture[T](action)

  class EffDbActionExecutorDBIOFuture[T](private val action: DBIO[T])(implicit db: JdbcProfile#Backend#Database,
                                                                      profile: JdbcProfile)
      extends EffectsDatabaseActionExecutor[DBIO, TimedFuture, T] {

    def execute[R: _tOut]: Eff[R, T] = {
      import profile.api._
      FutureEffect.fromFuture(db.run(action.transactionally))
    }
  }
}
