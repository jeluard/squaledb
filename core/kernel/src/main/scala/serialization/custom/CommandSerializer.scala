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
import transport.protocol.{Command, Verb}

/** Custom [[Serializer]] for [[Command]].
 *
 *  *-----*----*----*-----*-----*-----*-----*----*
 *  *     |    |    |     |     |     |     |    |
 *  * UUID|VERB|Arg#|Arg1L|Arg1B|Arg2L|Arg2B|... |
 *  *     |    |    |     |     |     |     |    |
 *  *-----*----*----*-----*-----*-----*-----*----*
 */
class CommandSerializer[T](verbSerializer: Serializer[Verb], argumentSerializers: T => Serializer[T]) extends Serializer[Command] {

  def serialize(command: Command): Array[Byte] = {
    val arguments = command.arguments
    val serializedVerb = verbSerializer.serialize(command.verb)
    val serializedArguments = new Array[Array[Byte]](command.arguments.length)
    var i = 0
    var serializedArgumentsLength = 0
    while (i < arguments.length) {
      val argument = arguments(i).asInstanceOf[T]
      val serializedArgument = argumentSerializers(argument).serialize(argument)
      serializedArgumentsLength += serializedArgument.length
      serializedArguments(i) = serializedArgument
      i += 1
    }
    val buffer = java.nio.ByteBuffer.allocate(Sizes.UUID.bytes + serializedVerb.length + Sizes.Int.bytes + serializedArgumentsLength*Sizes.Int.bytes + serializedArgumentsLength)
    appendUUID(command.uuid, buffer)
    buffer.put(serializedVerb)
    buffer.putInt(serializedArguments.length)
    i = 0
    while (i < serializedArguments.length) {
      val serializedArgument = serializedArguments(i)
      buffer.putInt(serializedArgument.length)
      buffer.put(serializedArgument)
      i += 1
    }
    buffer.array
  }

  def unserialize(bytes: Array[Byte]): Command = {
    val uuid = bytesToUUID(bytes)
    val verb = verbSerializer.unserialize(Arrays.slice(bytes, Sizes.UUID.bytes, Sizes.UUID.bytes + Sizes.Int.bytes))
    val argumentsCount = bytesToInt(bytes, Sizes.UUID.bytes + Sizes.Int.bytes)
    var accumulatedSerializedArgumentsLength = 0
    val arguments = for (i <- 0 until argumentsCount) yield {
      val index = Sizes.UUID.bytes + Sizes.Int.bytes + Sizes.Int.bytes + i * Sizes.Int.bytes + accumulatedSerializedArgumentsLength
      val serializedArgumentLength = bytesToInt(bytes, index)
      accumulatedSerializedArgumentsLength += serializedArgumentLength
      Arrays.slice(bytes, index + Sizes.Int.bytes, index + Sizes.Int.bytes + serializedArgumentLength)
    }
    new Command(uuid, verb, arguments.toArray)
  }

}

/** Specialized [[CommandSerializer]] whose arguments are all [[Array[Byte]]].
 */
class ByteArrayCommandSerializer(verbSerializer: Serializer[Verb]) extends CommandSerializer[Array[Byte]](verbSerializer, { argument: Any => new ByteArraySerializer() })