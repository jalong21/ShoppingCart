package models

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

  def applyAllCouponsToItems(items: Seq[Item], coupons: Seq[String]) = coupons
    .map(coupon => CouponProvider.getCoupon(coupon))
    .foldLeft[Seq[Item]](items)((items, coupon) =>
      coupon.couponFunction(items, coupon.priceMultiplyer, coupon.itemId))

  val coupons = Map[String, Coupon](
    ("15PerOff", Coupon(0.9, None, perOffEverything)), // 15% off
    ("10PerOffWaterCan", Coupon(0.9, Some(5), perOffItem)), // 10% off watering cans
    ("5PerOffWheelBarrow", Coupon(0.95, Some(12), perOffItem)),
    ("50PerOff2OrMoreSpade", Coupon(0.5, Some(2), perOffTwoOrMore)), // 50% off spades if you buy 2 or more
    ("50PerOffGroupOf3Trowel", Coupon(0.5, Some(7), perOffMultipleOfThree)) // 50% off trowel if you buy multiple of 3
  )

  def getCoupon(id: String) = coupons
    .filter(_._1.equals(id))
    .map(_._2)
    .head
}