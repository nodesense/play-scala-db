package models

import play.api.libs.json._

case class Product(id: String, name: String, price: Int)

// Custom formatter

object Product {
  // custom formatter as implicit
  implicit object ProductFormat extends  Format[Product] {
    def writes(product: Product): JsValue = {

      val json2 = Seq(
        "discount" -> JsNumber(10)
      )

      val productSeq = Seq(
        "id" -> JsString(product.id),
        "name" -> JsString(product.name),
        "price" -> JsNumber(product.price),
        "slug"  -> JsString(product.name.toLowerCase), // custom added
        "offer" -> JsObject(json2)
      )

      // {id: , name: , pr... offer: {discount: 10}}

      // JsObject(json2) // {discount: 10}
      JsObject(productSeq) // {discount: 10}
    }

    //de-serialization

    def reads(json: JsValue): JsResult[Product] = {
      val id = (json \ "id").as[String]
      val price = (json \ "price").as[Int]

      val name = (json \ "name").as[String]

      JsSuccess(Product(id, name, price))
    }

  }
}