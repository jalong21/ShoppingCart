package services

import Utils.Cache
import akka.actor.{Actor, Props, Timers}
import models._
import net.sf.ehcache.Element
import play.api.{Configuration, Logger}
import services.CartActor._

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
      Cache.getCache.put(new Element(cartUid, Cart(cartUid, name, state, Seq[Item](), Seq[String]())))
      sender ! cartUid
    }
    case AddItemToCart(cartId: String, itemId: Int) => {
      val cart = Cache.getCache.get(cartId).getObjectValue.asInstanceOf[Cart]
      val updatedItems: Seq[Item] = cart.items ++ Seq(ItemProvider.getItem(itemId))
      val newCart = Cart(cart.uuid, cart.name, cart.shippingState, updatedItems, cart.coupons)
      Cache.getCache.put(new Element(cart.uuid, newCart))
      sender ! true
    }
    case ApplyCouponToCart(couponId: String, cartId: String) => {
      Option(CouponProvider.getCoupon(couponId))
        .map(_ => {
          val cart = Cache.getCache.get(cartId).getObjectValue.asInstanceOf[Cart]
          val updatedCoupons: Seq[String] = cart.coupons ++ Seq(couponId)
          val newCart = Cart(cart.uuid, cart.name, cart.shippingState, cart.items, updatedCoupons)
          Cache.getCache.put(new Element(cart.uuid, newCart))
          sender ! true
        })
        .getOrElse({
          sender ! false
        })
    }
    case CheckoutCart(cartId: String) => {
      val cart = Cache.getCache.get(cartId).getObjectValue.asInstanceOf[Cart]
      cart.shippingState.map(state => {
        val couponedItems = cart
          .coupons
          .map(coupon => CouponProvider.getCoupon(coupon))
          .foldLeft[Seq[Item]](cart.items)((items, coupon) =>
            coupon.couponFunction(items, coupon.priceMultiplyer, coupon.itemId))
        val taxedTotalCost = couponedItems
          .foldLeft[Double](0)((runningTotal, item) => {
            //TODO: make list of states with individual tax amounts for this calculation
            runningTotal + item.price * 1.05
          })
        val calculatedCart = CheckedOutCart(taxedTotalCost, Cart(cart.uuid, cart.name, cart.shippingState, couponedItems, cart.coupons))
        sender ! Right(calculatedCart)
      })
        .getOrElse({
          sender ! Left("State Not Specified. Cannot Calculate Taxes.")
        })
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
