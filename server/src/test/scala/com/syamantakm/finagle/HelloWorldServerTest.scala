package com.syamantakm.finagle

import com.twitter.finagle.http.Status._
import com.twitter.finatra.http.test.EmbeddedHttpServer
import com.twitter.inject.server.FeatureTest

/**
 * @author syamantak.
 */
class HelloWorldServerTest extends FeatureTest {

  override val server = new EmbeddedHttpServer(new HelloWorldServer)

  "Server" should {
    "Say hi" in {
      server.httpGet(
        path = "/hi?name=Bob",
        andExpect = Ok,
        withBody = "Hello Bob")
    }
    "Say hi with json" in {
      server.httpGet(
        path = "/hi/Bob",
        andExpect = Ok,
        withBody = """{"name":"Bob", "id": 1}""")
    }
  }

}
