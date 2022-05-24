package controllers

import akka.actor.ActorSystem
import akka.pattern.ask
import akka.util.Timeout
import play.api.mvc._
import play.api.{Configuration, Logger}
import services.CartActor
import services.CartActor.CreateCart

import javax.inject.Inject
import scala.concurrent.Await
import scala.concurrent.duration.DurationInt

class CartController @Inject()(cc: ControllerComponents,
                               conf: Configuration) extends AbstractController(cc) {

  val log = Logger(this.getClass.getName)

  val system = ActorSystem("CartActorSystem")
  val cartActor = system.actorOf(CartActor.props(conf), name = "CartActor")
  implicit val askTimeout = Timeout(5.seconds)

  def createCart(name: String, state: Option[String]) = Action {
    log.warn(s"createCart - name: $name state: $state")
    val cartID = ask(cartActor, CreateCart(name, state))
      .mapTo[String]
    Ok(Await.result[String](cartID, 5.seconds))
  }

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

  def getItems() = Action {
    log.warn(s"getItems")
    val cartID = ask(cartActor, CreateCart("id", None))
      .mapTo[String]
    Ok(Await.result[String](cartID, 5.seconds))
  }

  def addItemToCart(itemID: Int, cartID: String) = Action {
    log.warn(s"addItemToCart - item: $itemID, cartID: $cartID")
    val cartID = ask(cartActor, CreateCart("id", None))
      .mapTo[String]
    Ok(Await.result[String](cartID, 5.seconds))
  }

  def removeItemFromCart(itemID: Int, cartID: String) = Action {
    log.warn(s"removeItemFromCart - item: $itemID, cartID: $cartID")
    val cartID = ask(cartActor, CreateCart("id", None))
      .mapTo[String]
    Ok(Await.result[String](cartID, 5.seconds))
  }

  def emptyCart(cartID: String) = Action {
    log.warn(s"emptyCart - cartID: $cartID")
    val cartID = ask(cartActor, CreateCart("id", None))
      .mapTo[String]
    Ok(Await.result[String](cartID, 5.seconds))
  }

  def applyCouponToCart(couponID: String, cartID: String) = Action {
    log.warn(s"applyCouponToCart - cartID: $cartID, couponID: $couponID")
    val cartID = ask(cartActor, CreateCart("id", None))
      .mapTo[String]
    Ok(Await.result[String](cartID, 5.seconds))
  }

  def checkOutCart(paymentId: String, cartID: String) = Action {
    log.warn(s"checkOutCart - cartID: $cartID, paymentId: $paymentId")
    val cartID = ask(cartActor, CreateCart("id", None))
      .mapTo[String]
    Ok(Await.result[String](cartID, 5.seconds))
  }
}