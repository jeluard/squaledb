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
import transport.protocol.{Dictionary, Verb}

/** Custom [[Serializer]] for [[Verb]] defined using a [[Dictionary]].
 *  TODO optimize byte sent based on Dictionary size.
 */
class VerbSerializer(implicit dictionnary: Dictionary) extends Serializer[Verb] {

  def serialize(verb: Verb): Array[Byte] = intToBytes(verb.id)

  def unserialize(bytes: Array[Byte]): Verb = dictionnary(bytesToInt(bytes)).asInstanceOf[Verb]//Safe cast as Dictionary defines Verb as Value type

}