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
  case class CheckoutCart(cartId: String, state: Option[String])
  case class GetItems()
}

class CartActor(conf: Configuration) extends Actor with Timers {

  val log = Logger(this.getClass.getName)

  override def receive: Receive = {
    case CreateCart(name: String, state: Option[String]) => Option(UUID.randomUUID().toString)
      .map(uuid => {
        Cache.addOrReplaceCart(Cart(uuid, name, state, Seq[Item](), Seq[String]()))
        sender ! uuid
      })
      .getOrElse(sender ! "Error creating UUID")

    case AddItemToCart(cartId: String, itemId: Int) => Cache.getCart(cartId)
      .map(cart => {
        val updatedItems: Seq[Item] = cart.items ++ Seq(ItemProvider.getItem(itemId))
        val newCart = Cart(cart.uuid, cart.name, cart.shippingState, updatedItems, cart.coupons)
        Cache.addOrReplaceCart(newCart)
        sender ! "Success!"
      })
      .getOrElse(sender ! "Error adding item to Cart")

    case ApplyCouponToCart(couponId: String, cartId: String) => Option(CouponProvider.getCoupon(couponId))
        .map(_ => Cache.getCart(cartId)
            .map(cart => {
              Cache.addOrReplaceCart(Cart(cart.uuid,
                cart.name,
                cart.shippingState,
                cart.items,
                cart.coupons ++ Seq(couponId)))
              sender ! "Success"
            })
        .getOrElse(sender ! "Cart Not Found!"))
        .getOrElse(sender ! "Coupon Not Found")

    case CheckoutCart(cartId: String, state: Option[String]) => Cache.getCart(cartId)
      .map(cart => {
        state
          .orElse(cart.shippingState)
          .map(state => {
            val couponedItems = CouponProvider.applyAllCouponsToItems(cart.items, cart.coupons)
            val taxedTotalCost = couponedItems
              .foldLeft[Double](0)((runningTotal, item) => {
                //TODO: make list of states with individual tax amounts for this calculation
                runningTotal + item.price * 1.05
              })
            val calculatedCart = CheckedOutCart(taxedTotalCost, Cart(cart.uuid, cart.name, cart.shippingState, couponedItems, cart.coupons))
            sender ! Right(calculatedCart)
          })
          .getOrElse(sender ! Left("State Not Specified. Cannot Calculate Taxes."))
      })
      .getOrElse(sender ! Left("Cart Not Found!"))

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
