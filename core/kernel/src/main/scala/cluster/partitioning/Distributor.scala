/*
 * Copyright 2011 julien.
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
 */

package org.squaledb
package cluster
package partitioning

import node.Identifier
import transport.{Dispatcher, Message, Response}

/** [[Exception]] thrown when [[Router]] failed to [[Router#route]] a [[Message]].
 */
class DistributionException(message: String, cause: RoutingException) extends Exception(message, cause)

/** Combine [[Partitioner]], [[Router]] and [[Dispatcher]] to send a [[Message]].
 */
class Distributor[I <: Message, O <: Response](partitioner: Partitioner[I], router: Router[I, O], resolver: Identifier => Dispatcher[I, O]) extends Function2[I, Set[Identifier], O] {

  final def apply(message: I, nodes: Set[Identifier]): O = try {
    router(message, partitioner(message, nodes).map(resolver))
  } catch {
    case e: RoutingException => throw new DistributionException("Failed to distribute <"+message+"> using <"+nodes+">", e)
  }

}