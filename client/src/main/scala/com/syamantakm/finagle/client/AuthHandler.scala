package com.syamantakm.finagle.client

import java.nio.charset.StandardCharsets
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.{Lock, ReentrantReadWriteLock}

import com.twitter.finagle.{SimpleFilter, Service}
import com.twitter.finagle.http.RequestBuilder
import com.twitter.util.{Future, Duration, Await, Promise}
import org.jboss.netty.handler.codec.http.{HttpRequest => Req, HttpResponse => Resp}

/**
 * @author syamantak.
 */
class AuthHandler extends SimpleFilter[Req, Resp] {

  var token: Option[String] = None
  val lock = new ReentrantReadWriteLock
  val readLock = lock.readLock()
  val writeLock = lock.writeLock()
  val authRequest = RequestBuilder().url("http://localhost:8888/auth").buildGet()

  private def getToken(client: Service[Req, Resp]): String = {
    try {
      readLock.tryLock
      token match {
        case Some(token) => {token}
        case None => {
          readLock.unlock()
          try {
            writeLock.tryLock(2, TimeUnit.SECONDS)
            token = Some(getStringResponse(authRequest, client))
          } finally {
            writeLock.unlock()
          }
          readLock.lock()
          token.get
        }
      }
    } finally {
      readLock.unlock()
    }
  }

  private def getStringResponse(authRequest: Req, client: Service[Req, Resp]): String = {
    val promise = Promise[String]
    val future = client(authRequest)
    future onSuccess { value =>
      promise.setValue(value.getContent.toString(StandardCharsets.UTF_8))
    } onFailure { exception =>
      promise.setException(exception)
    }

    Await.result(promise, Duration(2, TimeUnit.SECONDS))
  }

  override def apply(request: Req, service: Service[Req, Resp]) = {
    val token = getToken(service)
    println(s"Auth Token:$token")
    request.headers().add("auth", token)
    service(request)
  }
}