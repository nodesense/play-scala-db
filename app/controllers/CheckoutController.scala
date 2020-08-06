package controllers

// Inject, Singleton (Guice)
import javax.inject._
import play.api.mvc.{BaseController, ControllerComponents}
import models._
import play.api.mvc._;
import play.api.libs.json.Json

@Singleton
class CheckoutController @Inject() (val controllerComponents: ControllerComponents) extends  BaseController {


  // check getStocks
  //POST http://localhost:9000/checkout
  def checkout() = Action { request =>
    val json = request.body.asJson.get; // Play Json Object
    // convert to play json into Pojo
    val cart = json.as[Cart]

    Ok(Json.toJson(cart))
  }


}
