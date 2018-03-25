package com.mabrcosta.keysmanager.users.key.business

import java.util.UUID

import cats.data.Reader
import org.atnos.eff.{Fx, TimedFuture, |=}

package object api {

  type OwnerReader[A] = Reader[UUID, A]
  type _ownerReader[R] = OwnerReader |= R

  type ErrorEither[A] = Error Either A
  type _errorEither[R] = ErrorEither |= R

  type KeysStack = Fx.fx3[OwnerReader[?], ErrorEither[?], TimedFuture]

}
