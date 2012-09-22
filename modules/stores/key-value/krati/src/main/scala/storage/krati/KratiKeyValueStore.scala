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
package krati

import lifecycle.LifeCycle

import _root_.krati.store.DataStore
import org.apache.log4j.{Level, Logger}

/** [[KeyValueStore]] implementation relying on http://sna-projects.com/krati/.
 */
abstract class KratiKeyValueStore extends KeyValueStore[Array[Byte], Array[Byte]] with FlushablePersistentStore with LifeCycle {

  support.util.logging.log4j.installRedirection()

  protected val store: DataStore[Array[Byte], Array[Byte]]

  final def get(key: Array[Byte]): Option[Array[Byte]] = Option(store.get(key))

  final def put(key: Array[Byte], value: Array[Byte]) = store.put(key, value)

  final def remove(key: Array[Byte]) = store.delete(key)

  final def flush() = store.sync() //persist is asynchronous while sync is synchronous

  override def doClose = if (store.isOpen) store.close

}