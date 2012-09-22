/*
 *  Copyright 2010 julien.
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

import measure.Count

/** Read only view of a key/value based [[Store]].
 *  Allows to access indexed values from a key.
 */
trait ReadOnlyKeyValueStore[K, V] extends Store {

  /** @return value associated with key if any, [[None]] otherwise.
   */
  def get(key: K): Option[V]

  /**
   * Default implementation relying on [[get]].
   */
  def getAll(keys: Seq[K]): Seq[Option[V]] = for (key <- keys) yield get(key)

}

/** [[ReadOnlyKeyValueStore]] providing access to stored elements count.
 */
trait CountAwareKeyValueStore[K, V] extends ReadOnlyKeyValueStore[K, V] {

  /** @return number of elements stored.
   */
  def count: Count

}

/** Extends [[ReadOnlyKeyValueStore]] by allowing to put/remove elements.
 */
trait KeyValueStore[K, V] extends ReadOnlyKeyValueStore[K, V] {

  /** Puts value accessible via key.
   *
   *  @throws IllegalArgumentException if a value is already associated with key
   */
  def put(key: K, value: V)

  /** Default implementation relying on [[put]].
   */
  def putAll(maps: Map[K, V]) = maps.foreach(entry => put(entry._1, entry._2))

  /** Removes value previously associated with key.
   */
  def remove(key: K)

  /** Default implementation relying on [[remove]].
   */
  def removeAll(keys: Seq[K]) = keys.foreach(remove)

}

/** [[SynchronousPersistentStore]] [[KeyValueStore]] relying on [[Flushable#flush]] to offer synchronous persistency.
 *  Automatically call [[Flushable#flush]] after each destructive operations.
 */
trait SynchronousPersistentKeyValueStore[K, V] extends KeyValueStore[K, V] with SynchronousPersistentStore { this: FlushablePersistentStore =>

  override abstract def put(key: K, value: V) {
    super.put(key, value)
    flush()
  }

  /** Default implementation relying on [[put]].
   */
  override abstract def putAll(maps: Map[K, V]) {
    super.putAll(maps)
    flush()
  }

  /** Removes value previously associated with key.
   */
  override abstract def remove(key: K) {
    super.remove(key)
    flush()
  }

  /** Default implementation relying on [[remove]].
   */
  override abstract def removeAll(keys: Seq[K]) {
    super.removeAll(keys)
    flush()
  }

}