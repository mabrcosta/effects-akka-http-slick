package com.mabrcosta.keysmanager.users.key.business.api

import java.util.UUID

import com.mabrcosta.keysmanager.users.key.data.Key
import org.atnos.eff.{Eff, |=}

trait KeysService[TDBIO[_], TDBOut[_]] {

  type _tDBOut[R] = TDBOut |= R

  def getForOwner[R : _tDBOut : _ownerReader : _errorEither]: Eff[R, Seq[Key]]

  def addKey[R : _tDBOut : _ownerReader : _errorEither](keyValue: String): Eff[R, Key]

  def deleteKey[R : _tDBOut : _ownerReader : _errorEither](uid: UUID): Eff[R, Boolean]

}
