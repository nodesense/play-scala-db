package models

import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.json.Writes._
import play.api.libs.functional.syntax._

case class Item(id: Int, qty: Int, price: Int)
case class Customer(id: Int, name: String)
case class Order(id: Int,
                 customer: Customer,
                 items: Seq[Item])


object Item {
  implicit val itemFormat: Format[Item] = Json.format[Item]

  //  implicit val itemReads: Reads[Item] = (
//    (JsPath \ "id").read[Int] and
//      (JsPath \ "qty").read[Int] and
//      (JsPath \ "price").read[Int]
//
//    )(Item.apply _)
//
//  implicit val itemWrites: Writes[Item] = (
//    (JsPath \ "id").write[Int] and
//      (JsPath \ "name").write[Int]
//      and
//      (JsPath \ "price").write[Int]
//    )(unlift(Item.unapply))
//
//
//  implicit val listItemReads = Json.reads[Seq[Item]]
//  implicit val listItemWrites = Json.writes[Seq[Item]]

}



object Customer {
  implicit val customerReads: Reads[Customer] = (
    (JsPath \ "id").read[Int] and
      (JsPath \ "name").read[String]
    )(Customer.apply _)

  implicit val customerWrites: Writes[Customer] = (
    (JsPath \ "id").write[Int] and
      (JsPath \ "name").write[String]
    )(unlift(Customer.unapply))
}

object Order {

  implicit val orderReads: Reads[Order] = (
    (JsPath \ "id").read[Int] and
      (JsPath \ "customer").read[Customer] and
      (JsPath \ "items").read[Seq[Item]]
    )(Order.apply _)


  implicit val orderWrites: Writes[Order] = (
    (JsPath \ "id").write[Int] and
      (JsPath \ "customer").write[Customer] and
      (JsPath \ "items").write[Seq[Item]]
    )(unlift(Order.unapply))
}