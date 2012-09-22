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
package blueprints
package keyvalue
package api

/** Key/value store abstraction.
 */
trait KeyValueStore {

  /** Return value associated with `key`, if any.
   */
  def get(key: Array[Byte]): Option[Array[Byte]]

  /** Atomically remove and return value associated with `key`, if any.
   */
  def getAndRemove(key: Array[Byte]): Option[Array[Byte]]

  /** Assigns value to `key`.
   */
  def put(key: Array[Byte], value: Array[Byte])

  /** Remove value associated to `key`.
   */
  def remove(key: Array[Byte])

}