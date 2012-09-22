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
package eaio

import com.eaio.uuid.{UUID => EAIOUUID}
import Version._

class UUIDWrapper(uuid: EAIOUUID) extends UUID((uuid.time, uuid.clockSeqAndNode), V1) with Serializable

/** UUID Generator implementation based on EAIO UUID.
 *
 *  @see http://johannburkard.de/software/uuid/
 */
class EAIOGenerator extends Generator {

  def supports(version: Version) = version match {
    case V1 => true
    case _ => false
  }

  def apply() = new UUIDWrapper(new EAIOUUID())

  def parse(string: String) = new UUIDWrapper(new EAIOUUID(string))

}