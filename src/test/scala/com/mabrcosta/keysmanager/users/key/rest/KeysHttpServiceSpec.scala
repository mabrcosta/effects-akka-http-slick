package com.mabrcosta.keysmanager.users.key.rest

import java.util.UUID

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.mabrcosta.keysmanager.users.key.business.api
import com.mabrcosta.keysmanager.users.key.business.api.{KeysService, KeysStack, NotFound}
import com.mabrcosta.keysmanager.users.key.data.{Key, KeyData}
import org.atnos.eff.{Eff, EitherCreation, ExecutorServices, TimedFuture}
import org.mockito.{ArgumentMatchers, Mockito}
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{AsyncWordSpec, Matchers}
import slick.dbio.DBIO

import scala.concurrent.ExecutionContext

class KeysHttpServiceSpec extends AsyncWordSpec with ScalatestRouteTest with Matchers with MockitoSugar {

  import KeysJsonSupport._

  val keyService: KeysService[DBIO, TimedFuture] = mock[KeysService[DBIO, TimedFuture]]

  val keysHttpService =
    new KeysHttpService(keyService, ExecutionContext.global, ExecutorServices.schedulerFromGlobalExecutionContext)

  val uidOwnerStr = "0d3fcc37-d330-4c7c-8d82-128235617d7d"
  val uidOwner: UUID = UUID.fromString(uidOwnerStr)

  val basePath = s"/users/$uidOwnerStr/keys"

  "Listing owner keys" when {

    "the owner have no keys" should {
      "return an empty sequence" in {
        Mockito.when(keyService.getForOwner[KeysStack]).thenReturn(Eff.pure[KeysStack, Seq[Key]](Seq()))

        Get(basePath) ~> keysHttpService.routes ~> check {
          status shouldEqual StatusCodes.OK
          entityAs[Seq[Key]] shouldEqual Seq()
        }
      }
    }
    "the owner have 2 keys" should {
      "return a sequence with 2 elements" in {
        val values =
          Seq(Key(value = "key1", uidOwnerSubject = uidOwner), Key(value = "key2", uidOwnerSubject = uidOwner))
        Mockito.when(keyService.getForOwner[KeysStack]).thenReturn(Eff.pure[KeysStack, Seq[Key]](values))

        Get(basePath) ~> keysHttpService.routes ~> check {
          status shouldEqual StatusCodes.OK
          entityAs[Seq[Key]] shouldEqual values
        }
      }
    }
  }

  "Adding keys" when {
    "adding a new key value" should {
      "return a persisted key with that value" in {
        val keyValue = "key_value"
        val key = Key(value = keyValue, uidOwnerSubject = uidOwner)

        Mockito
          .when(
            keyService.addKey[KeysStack](ArgumentMatchers.eq(keyValue))(ArgumentMatchers.any(),
                                                                        ArgumentMatchers.any(),
                                                                        ArgumentMatchers.any()))
          .thenReturn(Eff.pure[KeysStack, Key](key))

        Post(basePath, KeyData(keyValue)) ~> keysHttpService.routes ~> check {
          status shouldEqual StatusCodes.OK
          entityAs[Key].value shouldEqual keyValue
        }
      }
    }
  }

  "Deleting keys" when {
    "deleting a non-existent key for owner and uid" should {
      "return a NotFound error" in {
        val uidKey = UUID.randomUUID()
        val error = NotFound(s"Unable to find key with uid $uidKey")

        Mockito
          .when(
            keyService.deleteKey[KeysStack](ArgumentMatchers.eq(uidKey))(ArgumentMatchers.any(),
              ArgumentMatchers.any(),
              ArgumentMatchers.any()))
          .thenReturn(EitherCreation.left[KeysStack, api.Error, Boolean](error))

        Delete(basePath + s"/$uidKey") ~> keysHttpService.routes ~> check {
          status shouldEqual StatusCodes.NotFound
          entityAs[String] shouldEqual error.message
        }
      }
    }
    "deleting a valid key for owner and uid" should {
      "return true" in {
        val uidKey = UUID.randomUUID()
        val error = NotFound(s"Unable to find key with uid $uidKey")

        Mockito
          .when(
            keyService.deleteKey[KeysStack](ArgumentMatchers.eq(uidKey))(ArgumentMatchers.any(),
              ArgumentMatchers.any(),
              ArgumentMatchers.any()))
          .thenReturn(Eff.pure[KeysStack, Boolean](true))

        Delete(basePath + s"/$uidKey") ~> keysHttpService.routes ~> check {
          status shouldEqual StatusCodes.OK
        }
      }
    }
  }

}
