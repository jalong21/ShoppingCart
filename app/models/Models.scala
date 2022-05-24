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

case class Coupon(priceMultiplyer: Double,
                  itemId: Option[Int],
                  couponFunction: ((Seq[Item], Double, Option[Int]) => Seq[Item]))

case class Cart(uuid: String, name: String, shippingState: String, items: Seq[Item], coupons: Seq[Int])
object Cart {
  implicit val jsonReads: Reads[Cart] = Json.reads[Cart]
}

object CouponProvider {

  def perOffEverything(items: Seq[Item], priceMultiplyer: Double, itemId: Option[Int] = None) = {
    items
      .map(item => Item(item.name, (item.price*priceMultiplyer), item.id))
  }

  def perOffItem(items: Seq[Item], priceMultiplyer: Double, itemId: Option[Int]) = {
    itemId.map(id => {
      items
        .map(item => item.id match {
          case currentId if currentId == id => Item(item.name, (item.price*priceMultiplyer), item.id)
          case _ => item
        })
    }).getOrElse(items)
  }

  def perOffTwoOrMore(items: Seq[Item], priceMultiplyer: Double, itemId: Option[Int]) = {
    itemId.map(id => {
      val groupedItems = items
        .groupBy[Int](_.id)
        .values
        .map(items => {
          if (items.size > 1) {

          }
        })


    }).getOrElse(items)
  }

  val Coupons = Seq[Coupon](
    Coupon(0.9, None, perOffEverything), // 15% off
    Coupon(0.9, Some(5), perOffItem), // 10% off watering cans
    Coupon(0.5, Some(2), perOffTwoOrMore) // 50% off spades if you buy 2 or more
  )
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