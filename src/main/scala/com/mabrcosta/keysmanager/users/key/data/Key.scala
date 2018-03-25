package com.mabrcosta.keysmanager.users.key.data

import java.time.Instant
import java.util.UUID

import com.mabrcosta.keysmanager.core.data.BaseRepositoryEntity

case class Key(id: Option[UUID] = Some(UUID.randomUUID()),
               value: String,
               uidOwnerSubject: UUID,
               uidCreatorSubject: Option[UUID] = None,
               uidLastModifierSubject: Option[UUID] = None,
               creationTimestamp: Instant = Instant.now(),
               updateTimestamp: Instant = Instant.now())
    extends BaseRepositoryEntity[Key, UUID] {

  override def withId(id: UUID): Key = copy(id = Some(id))
}
