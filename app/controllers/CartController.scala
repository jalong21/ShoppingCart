package controllers

import akka.actor.ActorSystem
import akka.pattern.ask
import akka.util.Timeout
import models.{CheckedOutCart, ItemList}
import play.api.libs.json.Json
import play.api.mvc._
import play.api.{Configuration, Logger}
import services.CartActor
import services.CartActor._

import javax.inject.Inject
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationInt

class CartController @Inject()(cc: ControllerComponents,
                               conf: Configuration) extends AbstractController(cc) {

  val log = Logger(this.getClass.getName)

  val system = ActorSystem("CartActForSystem")
  val cartActor = system.actorOf(CartActor.props(conf), name = "CartActor")
  implicit val askTimeout = Timeout(5.seconds)

  def createCart(name: String, state: Option[String]) = Action {
    log.warn(s"createCart - name: $name state: $state")
    val result = ask(cartActor, CreateCart(name, state))
      .mapTo[Either[Exception, String]]
      .map(cartResult => cartResult match {
        case Left(ex) => BadRequest(ex.getMessage)
        case Right(response) => Ok(response)
      })
    Await.result[Result](result, 5.seconds)
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
    val result = ask(cartActor, AddItemToCart(cartId, itemId))
      .mapTo[Either[Exception, String]]
      .map(addResult => addResult match {
        case Left(ex) => BadRequest(ex.getMessage)
        case Right(response) => Ok(response)
      })
    Await.result[Result](result, 5.seconds)
  }

  def applyCouponToCart(couponID: String, id: String) = Action {
    log.warn(s"applyCouponToCart - cartID: $id, couponID: $couponID")
    val result = ask(cartActor, ApplyCouponToCart(couponID, id))
      .mapTo[Either[Exception, String]]
      .map(couponResult => couponResult match {
        case Left(ex) => BadRequest(ex.getMessage)
        case Right(response) => Ok(response)
      })
    Await.result[Result](result, 5.seconds)
  }

  def checkoutCart(id: String, state: Option[String]) = Action {
    log.warn(s"checkOutCart - cartID: $id")
    val result = ask(cartActor, CheckoutCart(id, state))
      .mapTo[Either[Exception, CheckedOutCart]]
      .map(checkoutResult => checkoutResult match {
        case Left(ex) => BadRequest(ex.getMessage)
        case Right(cart) => Ok(Json.toJson[CheckedOutCart](cart))
      })
    Await.result[Result](result, 5.seconds)
  }

  // Below this line none of the endpoints are actually plugged in
//  def updateCart(id: String, state: Option[String]) = Action {
//    log.warn(s"updateCart - id: $id state: $state")
//    val cartID = ask(cartActor, CreateCart(id, state))
//      .mapTo[String]
//    Ok(Await.result[String](cartID, 5.seconds))
//  }
//
//  def deleteCart(id: String) = Action {
//    log.warn(s"deleteCart - id: $id")
//    val cartID = ask(cartActor, CreateCart(id, None))
//      .mapTo[String]
//    Ok(Await.result[String](cartID, 5.seconds))
//  }
//
//  def getCart(id: String) = Action {
//    log.warn(s"getCart - id: $id")
//    val cartID = ask(cartActor, CreateCart(id, None))
//      .mapTo[String]
//    Ok(Await.result[String](cartID, 5.seconds))
//  }
//
//  def removeItemFromCart(itemID: String, id: String) = Action {
//    log.warn(s"removeItemFromCart - item: $itemID, cartID: $id")
//    val cartID = ask(cartActor, CreateCart("id", None))
//      .mapTo[String]
//    Ok(Await.result[String](cartID, 5.seconds))
//  }
//
//  def emptyCart(id: String) = Action {
//    log.warn(s"emptyCart - cartID: $id")
//    val cartID = ask(cartActor, CreateCart("id", None))
//      .mapTo[String]
//    Ok(Await.result[String](cartID, 5.seconds))
//  }
}