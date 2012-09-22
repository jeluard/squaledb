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
package availability

import event.{Event, EventProducing}
import lifecycle.LifeCycle
import measure.Duration
import node.Identifier

sealed abstract class Availability
case object Unknown extends Availability
case object Reachable extends Availability
case object Unreachable extends Availability
case object HostUnreachable extends Availability
case object Down extends Availability

case class AvailabilityChangedEvent(detector: FailureDetector, val identifier: Identifier, val from: Availability, val to: Availability) extends Event(Option(detector))

/**
 *  @see http://citeseerx.ist.psu.edu/viewdoc/download?doi=10.1.1.29.9683&rep=rep1&type=pdf
 *  @see http://www.cs.cornell.edu/home/sam/FDpapers.html
 *  @see http://pine.cs.yale.edu/pinewiki/FailureDetectors
 *  @see http://portal.acm.org/citation.cfm?coll=GUIDE&dl=GUIDE&id=135451
 *  @see http://portal.acm.org/citation.cfm?coll=GUIDE&dl=GUIDE&id=226647
 *
 *  All [[Node]]s are assumed [[Up]] by default.
 */
trait FailureDetector extends LifeCycle with EventProducing[AvailabilityChangedEvent] {

  /** Reports that a [[Identifier]] has been observed.
   *
   *  @param node observed node
   *  @param from node which observed, None if observation is local
   */
  def nodeObserved(identifier: Identifier, from: Option[Identifier] = None)

  /** Reports that a Request has been successful.
   *
   *  @param node node on which the request has been executed
   *  @param time request execution time
   */
  def requestSucceeded(identifier: Identifier, time: Option[Duration] = None)

  /** Reports that a Request has been unsuccessful.
   *
   *  @param node node on which the request has been executed
   *  @param time request execution time
   */
  def requestFailed(identifier: Identifier, throwable: Throwable, time: Option[Duration] = None)

  protected final def fireAvailabilityChange(identifier: Identifier, from: Availability, to: Availability) = fire(AvailabilityChangedEvent(this, identifier, from, to))

}