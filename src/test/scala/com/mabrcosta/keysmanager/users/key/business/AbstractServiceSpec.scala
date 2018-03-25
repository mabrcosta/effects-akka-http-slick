package com.mabrcosta.keysmanager.users.key.business

import java.util.UUID

import com.mabrcosta.keysmanager.core.persistence.util.{EffectsDatabaseActionExecutor, EffectsDatabaseExecutor}
import com.mabrcosta.keysmanager.users.key.business.api.KeysStack
import org.atnos.eff.concurrent.Scheduler
import org.atnos.eff.future.{_future, fromFuture, runAsync}
import org.atnos.eff.syntax.all._
import org.atnos.eff.{Eff, ExecutorServices, TimedFuture}
import org.mockito.Mockito
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{Assertion, AsyncWordSpec}
import slick.dbio.{DBIO, DBIOAction}

import scala.concurrent.Future

trait AbstractServiceSpec extends AsyncWordSpec with MockitoSugar {

  implicit val scheduler: Scheduler = ExecutorServices.schedulerFromGlobalExecutionContext

  val effectsDatabaseExecutor: EffectsDatabaseExecutor[DBIO, TimedFuture] =
    mock[EffectsDatabaseExecutor[DBIO, TimedFuture]]

  def mockDbInteraction[T](methodCall: DBIO[T], returnValue: T): Unit = {
    val execDbAction = Future.successful(returnValue)
    val dbAction = DBIOAction.from(execDbAction)

    Mockito.when(methodCall).thenReturn(dbAction)
    mockDatabaseExecutor[KeysStack, T](dbAction, execDbAction)
  }

  def mockDatabaseExecutor[R: _future, T](action: DBIO[T], returnValue: Future[T]): Unit = {
    val actionExecutor = mock[EffectsDatabaseActionExecutor[DBIO, TimedFuture, T]]

    Mockito.when(actionExecutor.execute).thenReturn(fromFuture(returnValue))
    Mockito.when(effectsDatabaseExecutor.apply(action)).thenReturn(actionExecutor)
  }

  def assertRight[T](effect: Eff[KeysStack, T], uidOwner: UUID, valueAssertion: T => Assertion): Future[Assertion] = {

    assertFutureSuccess[T](effect, uidOwner, {
      case Left(error)  => fail(s"Failed either assertion with left error: ${error.message}")
      case Right(value) => valueAssertion(value)
    })
  }

  def assertLeft[T](effect: Eff[KeysStack, T],
                    uidOwner: UUID,
                    errorAssertion: api.Error => Assertion): Future[Assertion] = {

    assertFutureSuccess[T](effect, uidOwner, {
      case Left(error)  => errorAssertion(error)
      case Right(value) => fail(s"Failed either assertion with right value: $value")
    })
  }

  def assertFutureSuccess[T](effect: Eff[KeysStack, T],
                             uidOwner: UUID,
                             valueAssertion: Either[api.Error, T] => Assertion): Future[Assertion] = {
    runStack(effect, uidOwner).map(valueAssertion).recover {
      case ex: Throwable => {
        ex.printStackTrace()
        fail(s"Failed future assertion with exception ${ex.getMessage}")
      }
    }
  }

  def runStack[T](effect: Eff[KeysStack, T], uidOwner: UUID): Future[Either[api.Error, T]] = {
    runAsync(effect.runReader(uidOwner).runEither)
  }

}
