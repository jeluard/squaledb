/**
 * Copyright (C) 2010 julien
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.squaledb
package cluster
package routing
package sequential

import partitioning.{Router, RoutingException}
import support.util.logging.Logging
import transport.{Dispatcher, Message, Response}

/** [[Router]] implementation trying one by one each [[Dispatcher]] until one is successful.
 *  If all [[Dispatcher]]s fail a [[RoutingFailureException]] is thrown.
 */
class SequentialRouter[I <: Message, O <: Response] extends Router[I, O] with Logging {

  final def apply(message: I, candidates: Seq[Dispatcher[I, O]]): O = {
    if (candidates.isEmpty) throw new RoutingException("No reachable nodes")

    val candidate = candidates.head
    try {
      candidate.send(message)
    } catch {
      case e: InterruptedException => throw e
      case e => {
        finest("ROUTE_FAILED", candidate)

        apply(message, candidates.tail)
      }
    }
  }

}