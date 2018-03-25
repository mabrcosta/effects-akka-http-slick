package com.mabrcosta.keysmanager.core.rest

import java.time.Instant
import java.util.UUID

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.{DefaultJsonProtocol, DeserializationException, JsString, JsValue, JsonFormat}

trait CoreSprayJsonSupport extends SprayJsonSupport with DefaultJsonProtocol {

  implicit object UUIDFormat extends JsonFormat[UUID] {
    def write(uuid: UUID) = JsString(uuid.toString)
    def read(value: JsValue): UUID = {
      value match {
        case JsString(uuid) => UUID.fromString(uuid)
        case _              => throw DeserializationException("Expected hexadecimal UUID string")
      }
    }
  }

  implicit object InstantFormat extends JsonFormat[Instant] {
    import java.time.format.DateTimeFormatter
    val formatter: DateTimeFormatter = DateTimeFormatter.ISO_INSTANT

    def write(instant: Instant) = JsString(instant.toString)
    def read(value: JsValue): Instant = {
      value match {
        case JsString(instant) => Instant.from(formatter.parse(instant))
        case _                 => throw DeserializationException("Expected hexadecimal UUID string")
      }
    }
  }
}
