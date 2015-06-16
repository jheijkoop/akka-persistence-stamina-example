package org.leipie

import akka.actor.{ActorSystem, Props}
import akka.persistence.PersistentActor

import scala.concurrent.duration.DurationInt

object Main extends App {
  val system = ActorSystem("DemoSystem")

  val parrot = system.actorOf(Parrot.props)

  parrot ! "Hello"
  parrot ! ","
  parrot ! "World"

  import system.dispatcher
  system.scheduler.scheduleOnce(1 second) {
    system.shutdown()
  }
}

class Parrot extends PersistentActor {
  var count = 0
  override def receive = {
    case message: String => {
      count = count + 1
      println(s"$count: $message")
    }
  }
}

object Parrot {
  val props = Props[Parrot]
}