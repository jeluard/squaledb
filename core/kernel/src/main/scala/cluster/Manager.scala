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
package cluster

import event.{Event, EventProducing}
import node.Identifier

sealed abstract class MembershipEvent(manager: Manager, val identifier: Identifier) extends Event(Option(manager))
case class NodeJoinedEvent(manager: Manager, override val identifier: Identifier) extends MembershipEvent(manager, identifier)
case class NodeLeftEvent(manager: Manager, override val identifier: Identifier) extends MembershipEvent(manager, identifier)
case class ClusterOpenedEvent(manager: Manager, override val identifier: Identifier) extends MembershipEvent(manager, identifier)
case class ClusterClosedEvent(manager: Manager, override val identifier: Identifier) extends MembershipEvent(manager, identifier)

/** Handle cluster lifecycle. [[Node]] are registered/unregistered through this abstraction.
 */
trait Manager extends EventProducing[MembershipEvent] {

  def nodes: Set[Identifier]

  def isRegistered(identifier: Identifier) = nodes.contains(identifier)

  /** Register provided [[Identifier]].
   */
  def register(identifier: Identifier) = {
    require(!isRegistered(identifier), "Cannot register already registered <"+identifier+">")

    add(identifier)
  }

  protected def add(identifier: Identifier)

  /** Unregister provided [[Identifier]].
   */
  def unregister(identifier: Identifier) = {
    require(!isRegistered(identifier), "Cannot unregister not registered <"+identifier+">")

    remove(identifier)
  }

  protected def remove(identifier: Identifier)

}