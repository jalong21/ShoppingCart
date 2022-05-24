package models

import play.api.libs.json._

import java.util.UUID

case class ItemList(items: Seq[Item])
object ItemList {
  implicit val jsonReads: Reads[ItemList] = Json.reads[ItemList]
  implicit val writes = Json.writes[ItemList]
}

case class Item(name: String, price: Double, id: Int)
object Item {
  implicit val jsonReads: Reads[Item] = Json.reads[Item]
  implicit val jsonWrites: Writes[Item] = Json.writes[Item]
}

case class Cart(uuid: UUID, shippingState: String, items: Seq[Item])
object Cart {
  implicit val jsonReads: Reads[Cart] = Json.reads[Cart]
}

object ItemProvider {
  val items = Seq[Item](
    Item("Hedge Shears", 68.50, 1),
    Item("Spade", 44.95, 2),
    Item("Rake", 69.95, 3),
    Item("Pruning Shears", 44.95, 4),
    Item("Watering Can", 56.95, 5),
    Item("Weeding Tool Set", 124.95, 6),
    Item("Hand Trowel", 34.95, 7),
    Item("Garden Gloves", 32.95, 8),
    Item("Digging Shovel", 45.95, 9),
    Item("Garden Hoe", 45.95, 10),
    Item("Water Hose", 32.95, 11),
    Item("Wheelbarrow", 110.00, 12),
    Item("Pruning Saw", 64.95, 13)
  )
}