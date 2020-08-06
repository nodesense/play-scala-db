package models
import play.api.libs.json.{Format, Json}
import play.api.libs.json.Reads._
import play.api.libs.json.Writes._
import play.api.libs.functional.syntax._


case class Item(id: Long, order_id: Long,
                product_id: Long,
                brand_id: Long,
                qty: Int,
                price: Int,
                amount : Int)


object Item {
  implicit val itemFormat: Format[Item] = Json.format[Item]
}