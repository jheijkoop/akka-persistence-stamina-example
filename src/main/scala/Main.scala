package jheijkoop.stamina.demo

import akka.actor.{ActorSystem, Props}
import akka.persistence.PersistentActor
import fommil.sjs.FamilyFormats._
import jheijkoop.stamina.demo.Parrot.Incremented
import spray.json.lenses.JsonLenses._
import stamina.json._
import stamina.{Persistable, StaminaAkkaSerializer, V1, V2}

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

  val parrotPersister = persister[Incremented, V2](
    "increment",
    from[V1]
      .to[V2](_.update('message ! set[String]("[empty message]")))
  )
}

class ParrotSerializer extends StaminaAkkaSerializer(Parrot.parrotPersister)
