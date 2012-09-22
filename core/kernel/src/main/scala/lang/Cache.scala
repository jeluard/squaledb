package org.squaledb
package lang

import support.util.logging.Logging

import java.io.Closeable
import java.util.concurrent.ConcurrentHashMap

import support.collection._

/** Cache for complex object creation. Once created, objects are removed from memory only when invalidated.
 *
 *  @see http://blog.boundary.com/2011/05/03/atomicmap-solutions.html for inspiration/discussion of alternatives
 */
abstract class Cache[K, V] extends Function1[K, V] with Logging {

  /** [[V]] wrapper providing lazy value calculation.
   */
  private[this] class Lazy[V](value: => V) extends Proxy {

    def self = value // for proxy
    // Need to be careful with Proxy; any call to toString,
    // hashCode, or equals will force our call-by-name value.

    /** Get the value out of the Lazy.  Value will be evaluated exactly once. */
    lazy val get: V = value

  }

  private[this] val cache = new ConcurrentHashMap[K, Lazy[V]]()

  /** @return `value` associated to `key`. A new `value` will be created if not already in cache.
   *  @see [[#create]]
   */
  final def apply(key: K): V = {
    val wrappedValue = new Lazy(create(key))
    cache.putIfAbsent(key, wrappedValue) match {
      case null => wrappedValue.get
      case existingWrappedValue => existingWrappedValue.get
    }
  }

  /** @return `value` for `key`. Only called when `value` is not already cached.
   */
  protected def create(key: K): V

  /** Invalidate `value` associated to `key`, if any.
   *  Invalidated value will then be passed to [[#dispose]].
   */
  final def invalidate(key: K) = dispose(cache.remove(key).get)

  /** Dispose an invalidated `value`.
   *  @see [[#close]]
   */
  protected def dispose(value: V) = ()

  /** Dispose all `values`. Exception are intercepted and logged.
   */
  final def dispose(): Unit = synchronized {
    import collection.JavaConverters
    JavaConverters.collectionAsScalaIterableConverter(cache.values).asScala.safeForeach("DISPOSE_FAILED") { value =>
      dispose(value.get)
    }
    cache.clear
  }

}