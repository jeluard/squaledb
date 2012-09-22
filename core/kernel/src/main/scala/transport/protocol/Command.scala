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
package transport
package protocol

import uuid.UUID

import scala.Enumeration

/** [[Command]] verbs.
 */
trait Verb {
  def id: Int
}

/** Facility for [[Verb]] definitions.
 *
 *  @sample {{
 *    object MyDictionnary extends Dictionary {
 *      val Verb1, Verb2 = Verb
 *    }
 *  }}
 */
abstract class Dictionary extends Enumeration {
 protected final class VerbVal extends Val with Verb
 protected final def Verb = new VerbVal
}

/** Encapsulate details about a [[Command]] to be remotely executed.
 *  Suitable for [[Verb]] based protocol.
 */
class Command(override val uuid: UUID, val verb: Verb, val arguments: Array[Any]) extends Message(uuid)

/** Extractor giving access to parameters.
 */
class ParametersExtractor(verb: Verb) {
  //Cannot use Type parameters. See https://issues.scala-lang.org/browse/SI-884
  def unapplySeq(command: Command): Option[Seq[Any]] = if (command.verb == verb) Some(command.arguments.toSeq) else None
}