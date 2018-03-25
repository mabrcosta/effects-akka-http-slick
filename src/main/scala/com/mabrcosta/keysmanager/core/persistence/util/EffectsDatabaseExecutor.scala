package com.mabrcosta.keysmanager.core.persistence.util

import org.atnos.eff.{Eff, |=}

trait EffectsDatabaseExecutor[TDBIO[_], TOut[_]] {
  implicit def apply[T](action: TDBIO[T]): EffectsDatabaseActionExecutor[TDBIO, TOut, T]
}

trait EffectsDatabaseActionExecutor[TDBIO[_], TOut[_], T] {
  type _tOut[R] = TOut |= R

  def execute[R: _tOut]: Eff[R, T]
}
