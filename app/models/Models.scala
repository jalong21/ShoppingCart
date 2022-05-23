package models

import play.api.libs.json.{Json, Reads}

import java.util.UUID

case class Item(name: String, price: Float)
object Item {
  implicit val jsonReads: Reads[Item] = Json.reads[Item]
}

case class Cart(uuid: UUID, shippingState: String, items: Seq[Item])
object Cart {
  implicit val jsonReads: Reads[Cart] = Json.reads[Cart]
}
