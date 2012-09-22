/*
 *  Copyright 2011 julien.
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */

package org.squaledb
package storage

import support.management.hotspot.performance.Counter
import support.management.hotspot.performance.Counter._

/** [[ReadOnlyKeyValueStore]] mixin instrumenting calls to [[ReadOnlyKeyValueStore#get(K)]] and [[ReadOnlyKeyValueStore#getAll(Seq[K])]].
 */
trait InstrumentedReadOnlyKeyValueStore[K, V] extends ReadOnlyKeyValueStore[K, V] {

  private [this] final val DefaultGetCounterName = "store.get"
  protected val getCounterName = DefaultGetCounterName
  private [this] final val getCounter = new Counter(getCounterName)
  private [this] final val DefaultGetAllCounterName = "store.getAll"
  protected val getAllCounterName = DefaultGetAllCounterName
  private [this] val getAllCounter = new Counter(getAllCounterName)

  override abstract def get(key: K) = counting(getCounter) {
    super.get(key)
  }

  override abstract def getAll(keys: Seq[K]) = counting(getAllCounter) {
    super.getAll(keys)
  }

}

/** [[CountAwareKeyValueStore]] mixin instrumenting calls to [[CountAwareKeyValueStore#count()]].
 */
trait InstrumentedCountAwareKeyValueStore[K, V] extends InstrumentedReadOnlyKeyValueStore[K, V] with CountAwareKeyValueStore[K, V] {

  private [this] final val DefaultCounterName = "store.count"
  protected val countCounterName = DefaultCounterName
  private [this] final val countCounter = new Counter(countCounterName)

  override abstract def count() = counting(countCounter) {
    super.count
  }

}

/** [[KeyValueStore]] mixin instrumenting calls to [[ReadOnlyKeyValueStore#get(K)]], [[ReadOnlyKeyValueStore#getAll(Seq[K])]]
 *  [[ReadOnlyKeyValueStore#put(K, V)]], [[ReadOnlyKeyValueStore#putAll(Map[K, V])]],
 *  [[ReadOnlyKeyValueStore#remove(K)]] and [[ReadOnlyKeyValueStore#removeAll(Seq[K])]].
 */
trait InstrumentedKeyValueStore[K, V] extends InstrumentedReadOnlyKeyValueStore[K, V] with KeyValueStore[K, V] {

  private [this] final val DefaultPutCounterName = "store.put"
  protected val putCounterName = DefaultPutCounterName
  private [this] final val putCounter = new Counter(putCounterName)
  private [this] final val DefaultPutAllCounterName = "store.putAll"
  protected val putAllCounterName = DefaultPutAllCounterName
  private [this] final val putAllCounter = new Counter(putAllCounterName)
  private [this] final val DefaultRemoveCounterName = "store.remove"
  protected val removeCounterName = DefaultRemoveCounterName
  private [this] final val removeCounter = new Counter(removeCounterName)
  private [this] final val DefaultRemoveAllCounterName = "store.removeAll"
  protected val removeAllCounterName = DefaultRemoveAllCounterName
  private [this] final val removeAllCounter = new Counter(removeAllCounterName)

  override abstract def put(key: K, value: V) = counting(putCounter) {
    super.put(key, value)
  }

  override abstract def putAll(maps: Map[K, V]) = counting(putAllCounter) {
    super.putAll(maps)
  }

  override abstract def remove(key: K) = counting(removeCounter) {
    super.remove(key)
  }

  override abstract def removeAll(keys: Seq[K]) = counting(removeAllCounter) {
    super.removeAll(keys)
  }
}