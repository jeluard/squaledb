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
package serialization
package custom

import serialization.Serializer
import support.lang._
import transport.{Ack, Reply, Response}

/** Custom [[Serializer]] for [[Response]].
 *
 *  For Ack:
 *  *-----*
 *  *     |
 *  * UUID|
 *  *     |
 *  *-----*
 *
 *  For Reply:
 *  *-----*----*
 *  *     |    |
 *  * UUID| B  |
 *  *     |    |
 *  *-----*----*
 */
class ResponseSerializer[T](serializer: Serializer[T]) extends Serializer[Response] {

  def serialize(response: Response): Array[Byte] = response match {
    case Ack(uuid) => uuidToBytes(uuid)
    case Reply(uuid, value) => Arrays.concat(uuidToBytes(uuid), serializer.serialize(value.asInstanceOf[T]))
  }

  def unserialize(bytes: Array[Byte]): Response = if (bytes.size == Sizes.UUID.bytes) {
    Ack(bytesToUUID(bytes))
  } else {
    Reply(bytesToUUID(bytes), serializer.unserialize(Arrays.slice(bytes, Sizes.UUID.bytes)))
  }

}

/** Specialized [[ResponseSerializer]] whose value is [[Array[Byte]]].
 */
class ByteArrayResponseSerializer extends ResponseSerializer[Array[Byte]](new ByteArraySerializer())