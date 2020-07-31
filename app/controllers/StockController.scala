package controllers

// Inject, Singleton (Guice)
import javax.inject._
import play.api.mvc.{BaseController, ControllerComponents}
import models._
import play.api.mvc._;
import play.api.libs.json.Json

@Singleton
class StockController @Inject() (val controllerComponents: ControllerComponents) extends  BaseController {

  // provides Writes/ Serializer and Reads /Deserializer automatically
  // good when json properties and case class properties are same
  implicit val stockFormat = Json.format[Stock]
  var stocks = Set.empty[Stock]

  stocks += Stock("1", "Phone 2", 200)
  stocks += Stock("2", "Phone 2", 200)
  stocks += Stock("3", "Phone 2", 200)

  def getStock(id: String) = Action {
    // .find( s => s.id == id)
    // _ is a placeholder for the iteratable
    val stock =  stocks.find(_.id == id)
    // this code returns OK or NotFound, that returned to back to action
    stock match {
      case Some(s) => Ok(Json.toJson(s))
      case _ =>   NotFound
    }
  }


  def getStocks() = Action {
    Ok(Json.toJson(stocks.toSeq))
  }

  //http://localhost:9000/createStock
  // Postman, POST method, Body/raw & then select Format JSON
  // { "id": "100", "name": "Product 100", "qty": 100 }

  // check getStocks
  //http://localhost:9000/getStocks
  def createStock() = Action { request =>
      val json = request.body.asJson.get; // Play Json Object
      // convert to play json into Pojo
      val stock = json.as[Stock]
      stocks += stock // FIXME: stocks not adding up
      Ok(Json.toJson(stock))
  }

  // FIXME:
//  def deleteStock(id: String) = Action {
//    stocks.find(_.id == id) foreach { stock => stocks -= stock}
//
//    Ok()
//  }

}
