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
package transport

import cluster.node.Identifier

/** Marker trait identifying [[Dispatcher]] sending [[Message]]s to a single destination.
 */
trait Unicast

/** Marker trait identifying [[Dispatcher]] sending [[Message]]s to multiple registered destinations.
 */
trait Multicast

/** Marker trait identifying [[Dispatcher]] sending [[Message]]s to all accessible destinations.
 */
trait Broadcast

/** Dispatch [[Message]]s to a remote [[Receiver]].
 */
trait Dispatcher[I <: Message, O <: Response] {

  val identifier: Identifier

  /** Send a [[Message]] to associated [[Receiver]].
   *  @return corresponding [[Response]]
   */
  def send(message: I): O

}