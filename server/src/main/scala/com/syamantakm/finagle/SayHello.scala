package com.syamantakm.finagle

/**
 * @author syamantak.
 */
case class HelloRequest(name: String)
case class AuthResponse(token: String)
case class SayHello(name: String, id: Long = 1)
