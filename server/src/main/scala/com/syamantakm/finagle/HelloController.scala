package com.syamantakm.finagle

import java.util.UUID

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
    request.getHttpRequest.headers.contains("auth") match {
      case true => {
        info(request.getContentString)
        SayHello("anonymous")
      }
      case false => {
        response.forbidden("No auth header")
      }
    }

  }

  get("/auth") { request: Request =>
    info("auth")
    AuthResponse(UUID.randomUUID.toString)

  }


}
