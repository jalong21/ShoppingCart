package services

import akka.actor.typed.ActorRef
import akka.actor.{Actor, ActorRef, Props, Timers}
import play.api.{Configuration, Logger}
import services.CartActor.CreateCart

import java.util.UUID


object CartActor {
  def props(conf: Configuration) = Props(classOf[CartActor], conf)

  case class CreateCart(name: String, state: Option[String])
}

class CartActor(conf: Configuration) extends Actor with Timers {

  val log = Logger(this.getClass.getName)

  override def receive: Receive = {
    case CreateCart(name: String, state: Option[String]) => {
      sender ! UUID.randomUUID().toString
    }
  }

  override def preStart(): Unit = {
    log.warn("actor starting")
    super.preStart()
  }

  override def postStop(): Unit = {
    log.warn("actor stopped")
    super.postStop()
  }
}