/*
 * Copyright 2010 julien.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * under the License.
 */

package org.squaledb
package blueprints
package keyvalue

import annotation.destructive
import transport.Message
import transport.protocol.{Command, Dictionary,  ParametersExtractor, Verb}
import uuid.UUID

import java.io.{Externalizable, ObjectInput, ObjectOutput}
import java.nio.ByteBuffer

/** Custom commands.
 */
sealed abstract class KeyValueCommand(uuid: UUID, verb: Verb, arguments: Array[Any]) extends Command(uuid, verb, arguments)
case class GetCommand(override val uuid: UUID, key: Array[Byte]) extends KeyValueCommand(uuid, implementation.KeyValueDictionary.Get, Array(key))
@destructive case class GetAndRemoveCommand(override val uuid: UUID, key: Array[Byte]) extends KeyValueCommand(uuid, implementation.KeyValueDictionary.Get, Array(key))
@destructive case class PutCommand(override val uuid: UUID, key: Array[Byte], value: Array[Byte]) extends KeyValueCommand(uuid, implementation.KeyValueDictionary.Put, Array(key, value))
@destructive case class RemoveCommand(override val uuid: UUID, key: Array[Byte]) extends KeyValueCommand(uuid, implementation.KeyValueDictionary.Remove, Array(key))

/** Store specific [[Command]]s and [[Verb]]s.
 */
package object implementation {

  /** Custom verbs.
   */
  implicit object KeyValueDictionary extends Dictionary {
    val Get, GetAndRemove, Put, Remove = Verb
  }

  /** Extractor helpers.
   */
  class KeyValueParametersExtractor(verb: Verb) extends ParametersExtractor(verb) {
    override def unapplySeq(command: Command): Option[Seq[Array[Byte]]] = {
      val arguments = super.unapplySeq(command).getOrElse(return None)
      if (arguments.forall(argument => argument.isInstanceOf[Array[Byte]])) {
        Option(arguments.asInstanceOf[Seq[Array[Byte]]])
      } else {
        None
      }
    }
  }
  val GetParametersExtractor = new KeyValueParametersExtractor(KeyValueDictionary.Get)
  val GetAndRemoveParametersExtractor = new KeyValueParametersExtractor(KeyValueDictionary.GetAndRemove)
  val PutParametersExtractor = new KeyValueParametersExtractor(KeyValueDictionary.Put)
  val RemoveParametersExtractor = new KeyValueParametersExtractor(KeyValueDictionary.Remove)

  /** Access key from a command.
   */
  object KeyExtractor {
    def unapply(command: Command): Option[Array[Byte]] = if (!command.arguments.isEmpty && command.arguments(0).isInstanceOf[Array[Byte]]) Option(command.arguments(0).asInstanceOf[Array[Byte]]) else None
  }

}