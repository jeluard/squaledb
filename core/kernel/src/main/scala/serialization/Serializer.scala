/**
 * Copyright (C) 2010 julien
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.squaledb
package serialization

/** Abstracts serialization/unserialization of an Object to a byte array.
 *
 *  Benchmark of several serialization technology can be found here:
 *  https://github.com/eishay/jvm-serializers/wiki
 */
trait Serializer[T] {

  /** Serialize provided Object into a byte array.
   *
   *  @param object
   *  @return
   */
  def serialize(any: T): Array[Byte]

  /** Unserialize a byte array into a T.
   *
   *  @param bytes
   *  @return
   */
  def unserialize(bytes: Array[Byte]): T

}