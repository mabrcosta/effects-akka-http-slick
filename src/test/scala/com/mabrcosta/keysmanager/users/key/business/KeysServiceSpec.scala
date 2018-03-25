package com.mabrcosta.keysmanager.users.key.business

import java.util.UUID

import com.mabrcosta.keysmanager.users.key.business.api.{KeysStack, NotFound}
import com.mabrcosta.keysmanager.users.key.data.Key
import com.mabrcosta.keysmanager.users.key.persistence.api.KeysDal
import org.atnos.eff.TimedFuture
import org.mockito.invocation.InvocationOnMock
import org.mockito.{ArgumentMatcher, ArgumentMatchers, Mockito}
import org.scalatest.AsyncWordSpec
import slick.dbio.{DBIO, DBIOAction}

import scala.concurrent.{ExecutionContext, Future}

class KeysServiceSpec extends AsyncWordSpec with AbstractServiceSpec {

  val keysDal: KeysDal[DBIO] = mock[KeysDal[DBIO]]
  val keyService = new KeysServiceImpl[DBIO, TimedFuture](keysDal, effectsDatabaseExecutor, executionContext)

  val uidOwner: UUID = UUID.randomUUID()

  "Listing owner keys" when {
    "the owner have no keys" should {
      "return an empty sequence" in {
        mockDbInteraction(keysDal.findForOwner(uidOwner), Seq[Key]())
        assertRight[Seq[Key]](keyService.getForOwner[KeysStack],
                              uidOwner,
                              result => if (result.isEmpty) succeed else fail(result.toString()))
      }
    }
    "the owner have 2 keys" should {
      "return a sequence with 2 elements" in {
        val values =
          Seq(Key(value = "key1", uidOwnerSubject = uidOwner), Key(value = "key2", uidOwnerSubject = uidOwner))
        mockDbInteraction(keysDal.findForOwner(uidOwner), values)
        assertRight[Seq[Key]](keyService.getForOwner[KeysStack],
                              uidOwner,
                              result => if (result.size == 2 && result == values) succeed else fail(result.toString()))
      }
    }
  }

  "Adding keys" when {
    "adding a new key value" should {
      "return a persisted key with that value" in {
        val keyValue = "key_value"
        val execDbAction = Future.successful(Key(value = keyValue, uidOwnerSubject = uidOwner))
        val dbAction = DBIOAction.from(execDbAction)

        Mockito
          .when(keysDal.save(ArgumentMatchers.any(classOf[Key]))(ArgumentMatchers.any[ExecutionContext]()))
          .thenAnswer((invocation: InvocationOnMock) => {
            val key = invocation.getArgument[Key](0)
            if (key.value == keyValue) dbAction else fail()
          })
        mockDatabaseExecutor[KeysStack, Key](dbAction, execDbAction)

        assertRight[Key](
          keyService.addKey[KeysStack](keyValue),
          uidOwner,
          result => if (keyValueArgumentMatcher(keyValue).matches(result)) succeed else fail(result.toString))
      }
    }
  }

  "Deleting keys" when {
    "deleting a non-existent key for owner and uid" should {
      "return a NotFound error" in {
        val uidKey = UUID.randomUUID()
        val response: Option[Key] = None
        mockDbInteraction(keysDal.findForOwner(uidKey, uidOwner), response)
        assertLeft[Boolean](keyService.deleteKey[KeysStack](uidKey), uidOwner, {
          case NotFound(_) => succeed
          case res         => fail(res.toString)
        })
      }
    }
    "deleting a valid key for owner and uid" should {
      "return true" in {
        val uidKey = UUID.randomUUID()
        val response = Key(id = Some(uidKey), value = "key_value", uidOwnerSubject = uidOwner)
        mockDbInteraction(keysDal.findForOwner(uidKey, uidOwner), Some(response))
        mockDbInteraction(keysDal.delete(response), response)
        assertRight[Boolean](keyService.deleteKey[KeysStack](uidKey), uidOwner, result => if (result) succeed else fail)
      }
    }
  }

  def keyValueArgumentMatcher(keyValue: String): ArgumentMatcher[Key] =
    (argument: Key) => if (keyValue == argument.value) true else false

}
