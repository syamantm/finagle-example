package com.syamantakm.finagle

/**
 * @author syamantak.
 */
case class HelloRequest(name: String)
case class SayHello(name: String, timestamp: Long = System.currentTimeMillis())
