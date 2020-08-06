// ProductController.scala
package controllers

import javax.inject.{Inject, Singleton}
import models.{Brand, Product, Stock}
import play.api.libs.functional.syntax.unlift
import play.api.libs.json.{JsPath, Json, Writes}
import play.api.mvc.{BaseController, ControllerComponents}
import play.api.libs.functional.syntax._
import play.api.libs.json._
import repositories.ProductRepository

import scala.concurrent.ExecutionContext


@Singleton
class ProductController @Inject() (repo: ProductRepository, val controllerComponents: ControllerComponents) (implicit ec: ExecutionContext) extends  BaseController {


  def getProduct(id: String) = Action {
    val product = Product(id.toLong, "Phone 1", 10000, 1)
    Ok(Json.toJson(product)) // POJO to JSON, Content-Type application/json
  }

  def getProducts = Action {
    val products = List(
                        Product(1, "Phone 1", 10000, 1),
                        Product(2, "Phone 2", 20000, 1)
                    )

    Ok(Json.toJson(products))
  }





  //http://localhost:9000/products/findAll/1/1000/0/100
  def filterProducts(brand_id: Long, price: Int, offset: Int, skip: Int ) = Action.async { implicit request =>
    repo.findAll(brand_id, price, offset, skip).map { products =>
      Ok(Json.toJson(products))
    }
      .recover {
        case e => InternalServerError(e.getMessage)
        // add as many typed exceptions handling
        case _ => BadRequest
      }
  }

  def paginatedProducts(brand_id: Long, price: Int, offset: Int, skip: Int ) = Action.async { implicit request =>
    repo.findAll2(brand_id, price, offset, skip).map { products =>
      Ok(Json.toJson(products))
    }
      .recover {
        case e => InternalServerError(e.getMessage)
        // add as many typed exceptions handling
        case _ => BadRequest
      }
  }




  def createProduct() = Action(parse.json[Product]) {
    implicit request => {
      println("request", request.body)

      // Json.obj("name" -> "", "city" -> "BLR", "id" -> 10).validate[Product]

      Ok("done " + request.body.name)
    }
  }

}