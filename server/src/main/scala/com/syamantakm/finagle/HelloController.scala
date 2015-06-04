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

  post("/hello") { request: Request =>
    info("hello")
    info(request.getContentString)
    SayHello("anonymous")
  }


}
