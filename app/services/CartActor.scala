package services

import Utils.Cache
import akka.actor.typed.ActorRef
import akka.actor.{Actor, ActorRef, Props, Timers}
import models.{Cart, Item, ItemList, ItemProvider}
import net.sf.ehcache.Element
import play.api.{Configuration, Logger}
import services.CartActor.{CreateCart, GetItems}

import java.util.UUID


object CartActor {
  def props(conf: Configuration) = Props(classOf[CartActor], conf)

  case class CreateCart(name: String, state: Option[String])
  case class UpdateCart(id: String, state: Option[String])
  case class DeleteCart(id: String)
  case class GetCart(id: String)
  case class AddItemToCart(cartId: String, itemId: Int)
  case class RemoveItemFromCart(cartId: String, itemId: Int)
  case class EmptyCart(cartId: String)
  case class ApplyCouponToCart(couponId: String, cartId: String)
  case class CheckoutCart(cartId: String)
  case class GetItems()
}

class CartActor(conf: Configuration) extends Actor with Timers {

  val log = Logger(this.getClass.getName)

  override def receive: Receive = {
    case CreateCart(name: String, state: Option[String]) => {
      val cartUid = UUID.randomUUID().toString
      Cache.getCache.put(new Element(cartUid, Cart(cartUid, name, state.getOrElse("none"), Seq[Item]())))
      sender ! cartUid
    }
    case GetItems() => {
      sender ! ItemList(ItemProvider.items)
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
