package goto

import akka.actor.{Actor, ActorSystem, Props}

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

class Parrot extends Actor {
  var count = 0

  def receive = {
    case message: String => println(s"$count: $message")
  }
}

object Parrot {
  val props = Props[Parrot]
}
