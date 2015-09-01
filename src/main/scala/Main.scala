package stamina.demo

import akka.actor.{ActorSystem, Props}
import akka.persistence.PersistentActor
import spray.json.lenses.JsonLenses._
import stamina.demo.Parrot.Incremented
import stamina.json.SprayJsonMacros._
import stamina.json._
import stamina.{V2, Persistable, Persisters, StaminaAkkaSerializer, V1, V3}

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
    case event: Incremented =>
      update(event)
      print(event)
  }

  override def receiveCommand: Receive = {
    case message: String =>
      persist(Incremented(count + 1, message))(incremented => update(incremented))
      println(s"$count: $message")
  }

  def update: Receive = {
    case Incremented(newCount, message) => count = newCount
  }

  override def persistenceId = "parrot"
}

object Parrot {
  case class Incremented(count: Int, message: String) extends Persistable
  val props = Props[Parrot]

  val parrotPersister = persister[Incremented, V3](
    "increment",
    from[V1]
      .to[V2](_.update('message ! set[String]("[empty message]"))
      .to[V3](_.update('message ! set[String]("[empty message]")))
  )
}

class PricingAkkaSerializer(persisters: Persisters) extends StaminaAkkaSerializer(persisters) {
  def this() {
    this(Persisters(List(Parrot.parrotPersister)))
  }
}
