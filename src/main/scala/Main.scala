package goto

import akka.actor.{ActorSystem, Props}
import akka.persistence.PersistentActor
import goto.Parrot.Incremented

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

  override def receiveRecover: Receive = {
    case event: Incremented => update(event)
  }

  override def receiveCommand: Receive = {
    case message: String =>
      persist(Incremented(count + 1))(incremented => update(incremented))
      println(s"$count: $message")
  }

  def update: Receive = {
    case Incremented(newCount) => count = newCount
  }

  override def persistenceId = "parrot"
}

object Parrot {
  case class Incremented(count: Int)
  val props = Props[Parrot]
}