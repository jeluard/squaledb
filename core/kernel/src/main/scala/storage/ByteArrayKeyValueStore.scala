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

/** Wraps a [[Array[Byte]]] by providing [[Any#hashCode()]] and [[Any#equals(Any)]] implementations.
 *
 *  @see java.util.Arrays.hashCode(byte[])
 *  @see java.util.Arrays.equals(byte[], byte[])
 */
final class ByteArrayWrapper(val bytes: Array[Byte]) {

  import java.util.Arrays

  override def hashCode() = Arrays.hashCode(bytes)

  override def equals(that: Any) = that match {
    case other: ByteArrayWrapper => Arrays.equals(other.bytes, bytes)
    case _ => false
  }

}

/** Specific [[ReadOnlyKeyValueStore]] for [[Array[Byte]]]. Relies on [[ByteArrayWrapper]] to provide expected equality behavior.
 */
class ReadOnlyByteArrayKeyValueStoreWrapper(delegate: ReadOnlyKeyValueStore[Any, Array[Byte]]) extends ReadOnlyKeyValueStore[Array[Byte], Array[Byte]] {

  override final def get(key: Array[Byte]) = delegate.get(new ByteArrayWrapper(key))

  override final def getAll(keys: Seq[Array[Byte]]): Seq[Option[Array[Byte]]]= delegate.getAll(keys.map(key => new ByteArrayWrapper(key)))

}

/** Specific [[KeyValueStore]] for [[Array[Byte]]]. Relies on [[ByteArrayWrapper]] to provide expected equality behavior.
 */
class ByteArrayKeyValueStoreWrapper(delegate: KeyValueStore[Any, Array[Byte]]) extends ReadOnlyByteArrayKeyValueStoreWrapper(delegate) with KeyValueStore[Array[Byte], Array[Byte]] {

  override final def put(key: Array[Byte], value: Array[Byte]) = delegate.put(new ByteArrayWrapper(key), value)

  override final def putAll(maps: Map[Array[Byte], Array[Byte]]) = delegate.putAll(maps.map(entry => (new ByteArrayWrapper(entry._1), entry._2)))

  override final def remove(key: Array[Byte]) = delegate.remove(new ByteArrayWrapper(key))

  override final def removeAll(keys: Seq[Array[Byte]]) = delegate.removeAll(keys.map(key => new ByteArrayWrapper(key)))

}