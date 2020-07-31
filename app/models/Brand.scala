// models/Brand.scala
package models

import play.api.libs.json._
import play.api.libs.functional.syntax._

case class Brand(id: Long, name: String, year: Int);

object Brand {
  // composable Writes/Reads

  // extract  data field from a pojo object
  // pojo to json
  implicit val brandWrites: Writes[Brand] = (
    (JsPath \ "id").write[Long] and
      (JsPath \ "name").write[String] and
      (JsPath \ "year").write[Int]
    )(unlift(Brand.unapply))

  // create POJO from JSON
  // custom validations
  implicit val brandReads: Reads[Brand] = (
    (JsPath \ "id").read[Long] and
      (JsPath \ "name").read[String] and
        (JsPath \ "year").read[Int]
    )(Brand.apply _)

  // apply is a method using def

}
