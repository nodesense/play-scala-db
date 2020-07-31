// ProductController.scala
package controllers

import javax.inject.{Inject, Singleton}
import models.{Brand, Product, Stock}
import play.api.libs.functional.syntax.unlift
import play.api.libs.json.{JsPath, Json, Writes}
import play.api.mvc.{BaseController, ControllerComponents}
import play.api.libs.functional.syntax._
import play.api.libs.json._


@Singleton
class ProductController @Inject() (val controllerComponents: ControllerComponents) extends  BaseController {


  def getProduct(id: String) = Action {
    val product = Product(id, "Phone 1", 10000)
    Ok(Json.toJson(product)) // POJO to JSON, Content-Type application/json
  }

  def getProducts = Action {
    val products = List(
                        Product("1", "Phone 1", 10000),
                        Product("2", "Phone 2", 20000)
                    )

    Ok(Json.toJson(products))
  }

  def createProduct() = Action(parse.json[Product]) {
    implicit request => {
      println("request", request.body)

      // Json.obj("name" -> "", "city" -> "BLR", "id" -> 10).validate[Product]

      Ok("done " + request.body.name)
    }
  }

}