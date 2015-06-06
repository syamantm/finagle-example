package com.syamantakm.finagle.client


import java.nio.charset.StandardCharsets
import java.util.concurrent.TimeUnit

import com.twitter.finagle.http.RequestBuilder
import com.twitter.finagle.service.{TimeoutFilter, RetryingService, RetryPolicy, RetryingFilter}
import com.twitter.finagle.stats.NullStatsReceiver
import com.twitter.finagle.{Http, Service, SimpleFilter}
import com.twitter.util._
import org.jboss.netty.buffer.ChannelBuffers
import org.jboss.netty.handler.codec.http.HttpResponseStatus._
import org.jboss.netty.handler.codec.http._

/**
 * @author syamantak.
 */
/**
 * Convert HTTP 4xx and 5xx class responses into Exceptions.
 */
class InvalidRequest extends Exception

case class HelloRequest(name: String) {
  def toJson = {
    s"""{"name":"$name"}"""
  }
}

class HandleErrors extends SimpleFilter[HttpRequest, HttpResponse] {
  def apply(request: HttpRequest, service: Service[HttpRequest, HttpResponse]) = {
    // flatMap asynchronously responds to requests and can "map" them to both
    // success and failure values:
    service(request) flatMap { response =>
      response.getStatus match {
        case OK => Future.value(response)
        case FORBIDDEN => Future.exception(new InvalidRequest)
        case _ => Future.exception(new Exception(response.getStatus.getReasonPhrase))
      }
    }
  }
}

class HelloClient {

  def getStringResponse(request: HttpRequest, httpClient: Service[HttpRequest, HttpResponse]): String = {
    val promise = Promise[String]
    val future = httpClient(request)
    future onSuccess { value =>
      promise.setValue(value.getContent.toString(StandardCharsets.UTF_8))
    } onFailure { exception =>
      promise.setException(exception)
    }

    Await.result(promise, Duration(2, TimeUnit.SECONDS))
  }

  def sayHi(httpClient: Service[HttpRequest, HttpResponse]): String = {
    val request: HttpRequest = RequestBuilder().url("http://localhost:8888/hi?name=foo").buildGet()
    getStringResponse(request, httpClient)
  }

  def sayHello(httpClient: Service[HttpRequest, HttpResponse]): String = {
    val request: HttpRequest = RequestBuilder()
      .url("http://localhost:8888/hello")
      .buildPost(ChannelBuffers.copiedBuffer(HelloRequest("bob").toJson, StandardCharsets.UTF_8))
    getStringResponse(request, httpClient)
  }

}

object HelloClientMain extends App {

  val clientWithoutErrorHandling = Http.newService("localhost:8888")
  val handleErrors = new HandleErrors
  val retryRequests: SimpleFilter[HttpRequest, HttpResponse] = RetryingService.tries(2, new NullStatsReceiver)
  val requestTimesOut: SimpleFilter[HttpRequest, HttpResponse] = new TimeoutFilter(Duration(2, TimeUnit.SECONDS), new JavaTimer(false))

  val authHandler = new AuthHandler

  val client = handleErrors andThen retryRequests andThen requestTimesOut andThen authHandler andThen clientWithoutErrorHandling

  val helloClient = new HelloClient

  println(helloClient.sayHi(client))

  try {
    println(helloClient.sayHello(client))
  } catch {
    case exception: Exception => {
      exception.printStackTrace()
      System.exit(1)
    }

  }

  System.exit(0)
}
