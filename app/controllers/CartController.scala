package controllers

import akka.actor.ActorSystem
import akka.pattern.ask
import akka.util.Timeout
import play.api.{Configuration, Logger}
import play.api.mvc._
import services.CartActor
import services.CartActor.CreateCart

import java.util.UUID
import javax.inject.Inject
import scala.concurrent.{Await, Future}
import scala.concurrent.duration.DurationInt
import scala.util.{Failure, Success, Try}
import scala.concurrent.ExecutionContext.Implicits.global

class CartController @Inject()(cc: ControllerComponents,
                               conf: Configuration) extends AbstractController(cc) {

  val log = Logger(this.getClass.getName)

  val system = ActorSystem("CartActorSystem")
  val cartActor = system.actorOf(CartActor.props(conf), name = "CartActor")
  implicit val askTimeout = Timeout(5 seconds)

  def createCart(name: String, state: Option[String]) = Action {
    request => request.body.asJson
        .map( requestBody => {
          log.warn(s"createCart: ${requestBody.toString()}")
          val cartID = ask(cartActor, CreateCart(name, state))
            .mapTo[String]
          Ok(Await.result[String](cartID, 5 seconds))
        })
      .getOrElse(BadRequest("Payload Missing From Request"))
  }

}