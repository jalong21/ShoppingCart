package Utils

import net.sf.ehcache.CacheManager

object Cache {

  private val cacheManager = CacheManager.getInstance()
  cacheManager.addCacheIfAbsent("Cache")
  private val cache = cacheManager.getCache("Cache")

  def getCache = cache
}
