package Utils

import models.{Cart, Item}
import net.sf.ehcache.{CacheManager, Element}

object Cache {

  private val cacheManager = CacheManager.getInstance()
  cacheManager.addCacheIfAbsent("Cache")
  private val cache = cacheManager.getCache("Cache")

  def addOrReplaceCart(cart: Cart) =
    cache.put(new Element(cart.uuid, cart))

  def getCart(uuid: String) =
    Option(cache.get(uuid))
    .map(_.getObjectValue.asInstanceOf[Cart])
}
