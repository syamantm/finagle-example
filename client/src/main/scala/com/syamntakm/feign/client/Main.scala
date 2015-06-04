package com.syamntakm.feign.client

import java.util.concurrent.TimeUnit

import com.syamantakm.feign.client.HelloService
import feign.Feign
import akka.actor.Actor
import akka.actor.ActorSystem
import akka.actor.Props

/**
 * @author syamantak.
 */

class HelloActor extends Actor {

  def receive = {
    case Die(msg) =>  {
      println(msg)
      System.exit(0)
    }
    case SleepMessage(interval) => TimeUnit.MILLISECONDS.sleep(interval)
    case HelloMessage(msg) => println(msg)

  }
}

sealed trait ActorMessage

case class HelloMessage(val msg: String) extends ActorMessage {
  override def toString = "Received : " + msg
}

case class SleepMessage(val interval: Int) extends ActorMessage

case class Die(val msg: String) extends ActorMessage


object Main extends App {

  val helloService: HelloService = Feign.builder().target(classOf[HelloService], "http://localhost:8080")

  val system = ActorSystem("HelloSystem")

  // default Actor constructor
  val helloActor = system.actorOf(Props[HelloActor], name = "helloactor")

  // call api 10 times
  for (a <- 1 to 10) {
    helloActor ! HelloMessage(helloService.greetings)
    helloActor ! SleepMessage(500)
  }

  helloActor ! Die("Stopping server")
}

