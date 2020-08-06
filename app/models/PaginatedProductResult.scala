package models

import play.api.libs.json.{Format, Json}

case class PaginatedProductResult(totalCount: Int,
                           products: Seq[Product],
                           hasNextPage : Boolean)

object PaginatedProductResult {
  implicit val format: Format[PaginatedProductResult] = Json.format[PaginatedProductResult]
}