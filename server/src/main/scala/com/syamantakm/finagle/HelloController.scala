package com.syamantakm.finagle

import com.twitter.finagle.http.Request
import com.twitter.finatra.http.Controller

/**
 * @author syamantak.
 */
class HelloController extends Controller {

  get("/hi") { request: Request =>
    info("hi")
    "Hello " + request.params.getOrElse("name", "unnamed")
  }

  get("/hi/:name") { request: HelloRequest =>
    info("hi")
    SayHello(request.name)
  }


}
