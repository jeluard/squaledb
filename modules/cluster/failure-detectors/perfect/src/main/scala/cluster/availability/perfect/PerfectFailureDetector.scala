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
package cluster
package availability
package perfect

import measure.Duration
import node.Identifier

/** A [[FailureDetector]] purely based on synchronous communication timeouts.
 */
class PerfectFailureDetector extends FailureDetector {

  val nodes = collection.mutable.Map[Identifier, Availability]()

  protected final def updateAndFireIfNecessary(identifier: Identifier, availability: Availability) = synchronized {
    val previous = nodes.getOrElse(identifier, Unknown)
    nodes.update(identifier, availability)
    if (previous != availability) {
      fireAvailabilityChange(identifier, previous, availability)
    }
  }

  def nodeObserved(identifier: Identifier, from: Option[Identifier] = None) = {}

  def requestSucceeded(identifier: Identifier, time: Option[Duration] = None) = {
    updateAndFireIfNecessary(identifier, Reachable)
  }

  def requestFailed(identifier: Identifier, throwable: Throwable, time: Option[Duration] = None) = {
    updateAndFireIfNecessary(identifier, Unreachable)
  }

}