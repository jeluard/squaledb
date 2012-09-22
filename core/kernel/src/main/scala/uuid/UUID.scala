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
package uuid

import java.util.{UUID => JUUID}

object Version extends Enumeration {
  type Version = Value
  val V1, V2, V3, V4 = Value
}

import Version._

/** Abstraction for UUID.
 *
 *  @see http://www.iso.org/iso/catalogue_detail.htm?csnumber=2229
 */
class UUID(val bits: (Long, Long)/** (upper, lower)*/, val version: Version = V1) extends Serializable {

  def this(uuid: JUUID, version: Version) = this((uuid.getMostSignificantBits, uuid.getLeastSignificantBits), version)

  override def hashCode = bits.hashCode

  override def equals(any: Any) = {
    if (!any.isInstanceOf[UUID]) {
      false
    }

    val other = any.asInstanceOf[UUID]
    other.bits.equals(bits)
  }

}