package com.mabrcosta.keysmanager.users.key.business

import java.util.UUID

import com.mabrcosta.keysmanager.core.persistence.util.EffectsDatabaseExecutor
import com.mabrcosta.keysmanager.users.key.business.api.{KeysService, NotFound, _errorEither, _ownerReader}
import com.mabrcosta.keysmanager.users.key.data.Key
import com.mabrcosta.keysmanager.users.key.persistence.api.KeysDal
import javax.inject.Inject
import org.atnos.eff.Eff
import org.atnos.eff.EitherEffect._
import org.atnos.eff.ReaderEffect._
import slick.dbio.DBIO

import scala.concurrent.ExecutionContext

class KeysServiceImpl[TDBIO[_], TDBOut[_]] @Inject()(
      private val keysDal: KeysDal[DBIO],
      private val effectsDatabaseExecutor: EffectsDatabaseExecutor[DBIO, TDBOut],
      implicit val executionContext: ExecutionContext)
    extends KeysService[TDBIO, TDBOut] {

  import effectsDatabaseExecutor._

  override def getForOwner[R: _tDBOut: _ownerReader: _errorEither]: Eff[R, Seq[Key]] = {
    for {
      uidOwner <- ask
      keys <- keysDal.findForOwner(uidOwner).execute
    } yield keys
  }

  override def addKey[R: _tDBOut: _ownerReader: _errorEither](keyValue: String): Eff[R, Key] = {
    for {
      uidOwner <- ask
      key = Key(value = keyValue, uidOwnerSubject = uidOwner)
      res <- keysDal.save(key).execute
    } yield res
  }

  override def deleteKey[R: _tDBOut: _ownerReader: _errorEither](uid: UUID): Eff[R, Boolean] = {
    for {
      uidOwner <- ask
      keyOpt <- keysDal.findForOwner(uid, uidOwner).execute
      key <- if (keyOpt.isDefined) right(keyOpt.get)
      else left[R, api.Error, Key](NotFound(s"Unable to find key for uid $uid"))
      _ <- keysDal.delete(key).execute
    } yield true
  }

}
