package models

final case class Review(id: Int, title: String, body: String)
//
//class ReviewId private (val underlying: Int) extends AnyVal {
//  override def toString: String = underlying.toString
//}
//
//object ReviewId {
//  def apply(raw: String): ReviewId = {
//    require(raw != null)
//    new ReviewId(Integer.parseInt(raw))
//  }
//}
