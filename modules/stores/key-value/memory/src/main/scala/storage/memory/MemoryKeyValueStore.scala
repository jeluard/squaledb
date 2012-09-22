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
package memory

import measure.Count

import scala.collection.mutable.Map

/** [[KeyValueStore]] implementation using a [[Map]].
 */
class MemoryKeyValueStore[K, V](map: Map[K, V]) extends KeyValueStore[K, V] with CountAwareKeyValueStore[K, V] {

  def count = new Count(map.size)

  def get(key: K): Option[V] = map.get(key)

  def put(key: K, value: V) = map.put(key, value)

  def remove(key: K) = map.remove(key)

}