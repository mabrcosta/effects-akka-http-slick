package com.mabrcosta.keysmanager.users.key.rest

import com.mabrcosta.keysmanager.core.rest.CoreSprayJsonSupport
import com.mabrcosta.keysmanager.users.key.data.{Key, KeyData}
import spray.json.RootJsonFormat

trait KeysJsonSupport extends CoreSprayJsonSupport {
  implicit val keyFormat: RootJsonFormat[Key] = jsonFormat7(Key)
  implicit val keyDataFormat: RootJsonFormat[KeyData] = jsonFormat1(KeyData)
}

object KeysJsonSupport extends KeysJsonSupport