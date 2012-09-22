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
package cluster
package availability

import node.Identifier
import transport.{Dispatcher, Message, Response}

/** Report errors to [[FailureDetector]].
 */
class FailureReportingDispatcher[I <: Message, O <: Response](val delegate: Dispatcher[I, O], failureDetector: FailureDetector) extends Dispatcher[I, O] {

  val identifier = delegate.identifier

  /** Send a [[Message]] to associated [[Receiver]].
   *  @return an [[Exchange]] representing this operation execution
   */
  def send(message: I) = try {
    delegate.send(message)
  } catch {
    case e: InterruptedException => throw e
    case e => {
      failureDetector.requestFailed(identifier, e)

      throw e
    }
  }

}