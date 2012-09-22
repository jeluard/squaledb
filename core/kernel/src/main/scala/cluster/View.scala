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

import availability._
import lifecycle.LifeCycle
import node.Identifier
import support.util.logging.Logging

/** Provides a local view of cluster [[Node]] by combining [[Manager]] and [[FailureDetector]].
 */
class View(manager: Manager, failureDetector: FailureDetector) extends LifeCycle {

  private [this] final val failureDetectionListener: AvailabilityChangedEvent => Unit = event => {
    nodes += (event.identifier -> event.to)
    refreshReachableNodes()
  }

  private [this] final val membershipListener: MembershipEvent => Unit = event => {
    event match {
      case NodeJoinedEvent(_, identifier) => nodes += (identifier -> Unknown)
      case NodeLeftEvent(_, identifier) => nodes -= identifier
    }
    refreshReachableNodes()
  }

  private var nodes = Map[Identifier, Availability]()
  var reachableNodes: Set[Identifier] = Set()

  private def refreshReachableNodes() = reachableNodes = nodes.filter(_._2 == Reachable).keySet

  def availability(identifier: Identifier): Option[Availability] = nodes.get(identifier)

  override def doStart = {
    manager.addListener(membershipListener)
    failureDetector.addListener(failureDetectionListener)
  }

  override def doStop = {
    manager.removeListener(membershipListener)
    failureDetector.removeListener(failureDetectionListener)
  }

}