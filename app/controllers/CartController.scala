package controllers

import play.api.Logger
import play.api.mvc._

import javax.inject.Inject
import scala.util.{Failure, Success, Try}

class CartController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  val log = Logger(this.getClass.getName)

  def createCart(name: String, state: Option[String]) = Action {
    request => request.body.asJson
        .map( requestBody => {
          log.warn(s"requestBody: ${requestBody.toString()}")
          Try(

          ) match {
            case Success(tumbleRequest) => {
              Ok("nice")
            }
            case Failure(ex) => BadRequest(s"Error Parsing Request Body! ${ex.getMessage}")
          }
        }).getOrElse(BadRequest("Payload Missing From Request"))
  }

}