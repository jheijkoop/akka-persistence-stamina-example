package stamina.demo

import akka.actor.{ActorSystem, Props}
import akka.persistence.PersistentActor
import spray.json.lenses.JsonLenses._
import stamina.demo.Parrot.Incrementet
import stamina.json.SprayJsonMacros._
import stamina.json._
import stamina.{Persistable, Persisters, StaminaAkkaSerializer, V1, V2, V3}

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
    case e @ Incrementet(_count, message) =>
      count = _count
      println(e)
  }

  override def receiveCommand: Receive = {
    case message: String =>
      persist(Incrementet(count + 1, message)) { event =>
        count = event.count
        println(s"$count: ${event.foobar}")
      }
  }

  override def persistenceId: String = "parrot"
}

object Parrot {
  case class Incrementet(count: Int, foobar: String) extends Persistable

  val parrotPersister = persister[Incrementet, V3]("inc", from[V1]
    .to[V2](_.update('message ! set[String]("[empty message]")))
    .to[V3](_.update('message ! set[String]("[empty message2]"))))

  val props = Props[Parrot]
}

class PricingAkkaSerializer(persisters: Persisters) extends StaminaAkkaSerializer(persisters) {
  def this() {
    this(Persisters(List(Parrot.parrotPersister)))
  }
}
