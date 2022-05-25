package controllers

import akka.actor.ActorSystem
import akka.pattern.ask
import akka.util.Timeout
import models.{CheckedOutCart, ItemList}
import play.api.libs.json.Json
import play.api.mvc._
import play.api.{Configuration, Logger}
import services.CartActor
import services.CartActor.{AddItemToCart, ApplyCouponToCart, CheckoutCart, CreateCart, GetItems}

import javax.inject.Inject
import scala.concurrent.Await
import scala.concurrent.duration.DurationInt

class CartController @Inject()(cc: ControllerComponents,
                               conf: Configuration) extends AbstractController(cc) {

  val log = Logger(this.getClass.getName)

  val system = ActorSystem("CartActForSystem")
  val cartActor = system.actorOf(CartActor.props(conf), name = "CartActor")
  implicit val askTimeout = Timeout(5.seconds)

  def createCart(name: String, state: Option[String]) = Action {
    log.warn(s"createCart - name: $name state: $state")
    val cartID = ask(cartActor, CreateCart(name, state))
      .mapTo[String]
    Ok(Await.result[String](cartID, 5.seconds))
  }

  def getItems() = Action {
    log.warn(s"getItems")
    val cartID = ask(cartActor, GetItems())
      .mapTo[ItemList]
    val result = Await.result[ItemList](cartID, 5.seconds)
    Ok(Json.toJson(result))
  }

  def addItemToCart(cartId: String, itemId: Int) = Action {
    log.warn(s"addItemToCart - item: $itemId, cartID: $cartId")
    val cartID = ask(cartActor, AddItemToCart(cartId, itemId))
      .mapTo[String]
    Ok(Await.result[String](cartID, 5.seconds))
  }

  def applyCouponToCart(couponID: String, id: String) = Action {
    log.warn(s"applyCouponToCart - cartID: $id, couponID: $couponID")
    val cartID = ask(cartActor, ApplyCouponToCart(couponID, id))
      .mapTo[String]
    Ok(Await.result[String](cartID, 5.seconds))
  }

  def checkoutCart(id: String, state: Option[String]) = Action {
    log.warn(s"checkOutCart - cartID: $id")
    val checkOutResult = ask(cartActor, CheckoutCart(id, state))
      .mapTo[Either[String, CheckedOutCart]]
    Await.result[Either[String, CheckedOutCart]](checkOutResult, 5.seconds) match {
      case Left(errorMessage) => BadRequest(errorMessage)
      case Right(cart) => Ok(Json.toJson[CheckedOutCart](cart))
    }
  }

  // Below this line none of the endpoints are actually plugged in
  def updateCart(id: String, state: Option[String]) = Action {
    log.warn(s"updateCart - id: $id state: $state")
    val cartID = ask(cartActor, CreateCart(id, state))
      .mapTo[String]
    Ok(Await.result[String](cartID, 5.seconds))
  }

  def deleteCart(id: String) = Action {
    log.warn(s"deleteCart - id: $id")
    val cartID = ask(cartActor, CreateCart(id, None))
      .mapTo[String]
    Ok(Await.result[String](cartID, 5.seconds))
  }

  def getCart(id: String) = Action {
    log.warn(s"getCart - id: $id")
    val cartID = ask(cartActor, CreateCart(id, None))
      .mapTo[String]
    Ok(Await.result[String](cartID, 5.seconds))
  }

  def removeItemFromCart(itemID: String, id: String) = Action {
    log.warn(s"removeItemFromCart - item: $itemID, cartID: $id")
    val cartID = ask(cartActor, CreateCart("id", None))
      .mapTo[String]
    Ok(Await.result[String](cartID, 5.seconds))
  }

  def emptyCart(id: String) = Action {
    log.warn(s"emptyCart - cartID: $id")
    val cartID = ask(cartActor, CreateCart("id", None))
      .mapTo[String]
    Ok(Await.result[String](cartID, 5.seconds))
  }
}