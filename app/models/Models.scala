package models

import play.api.libs.json._

case class ItemList(items: Seq[Item])
object ItemList {
  implicit val writes = Json.writes[ItemList]
}

case class Item(name: String, price: Double, id: Int)
object Item {
  implicit val writes: Writes[Item] = Json.writes[Item]
}

case class Coupon(priceMultiplyer: Double,
                  itemId: Option[Int],
                  couponFunction: ((Seq[Item], Double, Option[Int]) => Seq[Item]))

case class Cart(uuid: String, name: String, shippingState: Option[String], items: Seq[Item], coupons: Seq[String])
object Cart {
  implicit val writes: Writes[Cart] = Json.writes[Cart]
}

case class CheckedOutCart(totalPrice: Double, cart: Cart)
object CheckedOutCart {
  implicit val writes: Writes[CheckedOutCart] = Json.writes[CheckedOutCart]
}

object CouponProvider {

  def perOffEverything(items: Seq[Item], priceMultiplyer: Double, itemId: Option[Int] = None): Seq[Item] = {
    items
      .map(item => Item(item.name, (item.price*priceMultiplyer), item.id))
  }

  def perOffItem(items: Seq[Item], priceMultiplyer: Double, itemId: Option[Int]): Seq[Item] = {
    itemId.map(id => {
      items
        .map(item => item.id match {
          case currentId if currentId == id => Item(item.name, (item.price*priceMultiplyer), item.id)
          case _ => item
        })
    }).getOrElse(items)
  }

  def perOffTwoOrMore(items: Seq[Item], priceMultiplyer: Double, itemId: Option[Int]): Seq[Item] = {
    itemId.map(id => {
      items
        .groupBy[Int](_.id)
        .values
        .map(items => items match {
          case items if (items.size > 1 && items.head.id == id) =>
            items.map(item => Item(item.name, (item.price*priceMultiplyer), item.id))
          case _ => items
        }).flatten[Item].toSeq
    }).getOrElse(items)
  }

  def perOffMultipleOfThree(items: Seq[Item], priceMultiplyer: Double, itemId: Option[Int]): Seq[Item] = {
    itemId.map(id => {
      items
        .groupBy[Int](_.id)
        .values
        .map(items => items match {
          case items if (items.size % 3 == 0 && items.head.id == id) =>
            items.map(item => Item(item.name, (item.price*priceMultiplyer), item.id))
          case _ => items
        }).flatten[Item].toSeq
    }).getOrElse(items)
  }

  val coupons = Map[String, Coupon](
    ("15PerOff", Coupon(0.9, None, perOffEverything)), // 15% off
    ("10PerOffWaterCan", Coupon(0.9, Some(5), perOffItem)), // 10% off watering cans
    ("50PerOff2OrMoreSpade", Coupon(0.5, Some(2), perOffTwoOrMore)), // 50% off spades if you buy 2 or more
    ("50PerOffGroupOf3Trowel", Coupon(0.5, Some(7), perOffMultipleOfThree)) // 50% off trowel if you buy multiple of 3
  )

  def getCoupon(id: String) = coupons
    .filter(_._1.equals(id))
    .map(_._2)
    .head
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

  def getItem(id: Int) = items
    .filter(_.id == id)
    .head
}