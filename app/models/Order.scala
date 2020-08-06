package models

import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.json.Writes._
import play.api.libs.functional.syntax._


case class Customer(id: Int, name: String)

case class Order(id: Int,
                 customer: Customer,
                 items: Seq[Item])

case class Cart(id: Int,
                 customer: Customer,
                 items: Seq[Item])


object Customer {
  implicit val customerFormat: Format[Customer] = Json.format[Customer]
}

object Order {
  implicit val orderFormat: Format[Order] = Json.format[Order]
}

object Cart {
  implicit val cartFormat: Format[Cart] = Json.format[Cart]
}
